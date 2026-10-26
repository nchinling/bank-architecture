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
    void createsCustomerWithApprovedKycAndProfile() throws Exception {
        // Distinct email so it does not clash with the seeded customers.
        String body = """
            {"fullName":"Eve Ng","email":"eve@example.com","nationalId":"S1112223E",
             "phone":"+65 9000 0000","city":"Singapore","country":"SG","occupation":"Analyst",
             "preferredChannel":"MOBILE"}
            """;
        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.kycStatus").value("APPROVED"))
                .andExpect(jsonPath("$.customerRef").exists())
                .andExpect(jsonPath("$.profile.phone").value("+65 9000 0000"))
                .andExpect(jsonPath("$.profile.occupation").value("Analyst"));
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
            {"fullName":"Frank Goh","email":"frank@example.com"}
            """;
        String response = mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String ref = response.replaceAll(".*\"customerRef\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/customers/" + ref))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("frank@example.com"));
    }

    @Test
    void returnsSeededCustomerProfile() throws Exception {
        // CUST-ALICE is seeded by V3 with a full profile.
        mockMvc.perform(get("/customers/CUST-ALICE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Alice Tan"))
                .andExpect(jsonPath("$.profile.city").value("Singapore"))
                .andExpect(jsonPath("$.profile.occupation").value("Software Engineer"));
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
