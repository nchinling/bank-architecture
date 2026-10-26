package sg.edu.rp.sandbox.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Validates that the generated OpenAPI document is served and lists the v1
 * customer endpoints - the contract students and the aggregated gateway UI rely on.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class OpenApiDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void openApiDocumentListsCustomerEndpoints() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Customer Service API"))
                .andExpect(jsonPath("$.paths./customers").exists())
                .andExpect(jsonPath("$.paths./customers/me").exists());
    }
}
