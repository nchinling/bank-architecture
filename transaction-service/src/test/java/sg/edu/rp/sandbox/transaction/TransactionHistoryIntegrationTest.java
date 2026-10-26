package sg.edu.rp.sandbox.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies transaction history is correct and owner-scoped against the seeded
 * ledger (TXN-SEED-0001..0003).
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/digital-bank"
})
class TransactionHistoryIntegrationTest {

    @TestConfiguration
    static class MockDecoderConfig {
        @Bean
        JwtDecoder jwtDecoder() {
            return mock(JwtDecoder.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    private static RequestPostProcessor asCustomer(String ref) {
        return jwt().jwt(j -> j.subject("sub-" + ref).claim("customer_ref", ref));
    }

    @Test
    void aliceSeesHerAccountHistory() throws Exception {
        // ACC-1001 has: a 150.00 CREDIT (from Bob) and a 3200.00 salary CREDIT.
        mockMvc.perform(get("/transactions/accounts/ACC-1001").with(asCustomer("CUST-ALICE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].direction").value("CREDIT"));
    }

    @Test
    void aliceCannotSeeBobsAccountHistory() throws Exception {
        // ACC-2001 belongs to Bob; Alice's customer_ref must yield an empty list.
        mockMvc.perform(get("/transactions/accounts/ACC-2001").with(asCustomer("CUST-ALICE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void dateFilterNarrowsResults() throws Exception {
        // Only the salary (2026-09-25) falls on/after 2026-09-10.
        mockMvc.perform(get("/transactions/accounts/ACC-1001")
                        .param("from", "2026-09-10T00:00:00Z")
                        .with(asCustomer("CUST-ALICE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].amount").value(3200.00));
    }

    @Test
    void anonymousIsRejected() throws Exception {
        mockMvc.perform(get("/transactions/accounts/ACC-1001"))
                .andExpect(status().isUnauthorized());
    }
}
