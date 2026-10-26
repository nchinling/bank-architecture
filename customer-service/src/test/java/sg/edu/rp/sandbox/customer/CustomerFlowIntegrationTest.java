package sg.edu.rp.sandbox.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end slice: create a customer -> simulated KYC runs -> status persisted
 * and retrievable. Covers the APPROVED and deterministic REJECTED (FRAUD) cases.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // this test targets business logic; security is covered separately
class CustomerFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsCustomerWithApprovedKyc() throws Exception {
        String body = """
            {"fullName":"Alice Tan","email":"alice@example.com","nationalId":"S1234567A"}
            """;
        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.kycStatus").value("APPROVED"))
                .andExpect(jsonPath("$.customerRef").exists());
    }

    @Test
    void rejectsKycForFraudName() throws Exception {
        String body = """
            {"fullName":"Fraud Test","email":"fraud@example.com"}
            """;
        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.kycStatus").value("REJECTED"));
    }

    @Test
    void retrievesCreatedCustomerByRef() throws Exception {
        String body = """
            {"fullName":"Bob Lim","email":"bob@example.com"}
            """;
        String response = mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String ref = response.replaceAll(".*\"customerRef\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/customers/" + ref))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@example.com"));
    }

    @Test
    void rejectsInvalidEmail() throws Exception {
        String body = """
            {"fullName":"Bad Email","email":"not-an-email"}
            """;
        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
    }
}
