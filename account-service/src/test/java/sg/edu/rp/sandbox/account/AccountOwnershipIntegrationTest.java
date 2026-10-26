package sg.edu.rp.sandbox.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies accounts are scoped to the authenticated customer:
 * <ul>
 *   <li>Alice sees exactly her seeded accounts,</li>
 *   <li>Alice cannot read Bob's account (treated as 404, not 403, to avoid leaking existence),</li>
 *   <li>anonymous callers are rejected (401).</li>
 * </ul>
 * Uses the seeded data (CUST-ALICE: ACC-1001, ACC-1002; CUST-BOB: ACC-2001, ACC-2002).
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/digital-bank"
})
class AccountOwnershipIntegrationTest {

    @TestConfiguration
    static class MockDecoderConfig {
        @Bean
        JwtDecoder jwtDecoder() {
            return mock(JwtDecoder.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    private static org.springframework.test.web.servlet.request.RequestPostProcessor asCustomer(String ref) {
        return jwt().jwt(j -> j.subject("sub-" + ref).claim("customer_ref", ref));
    }

    @Test
    void aliceSeesOnlyHerOwnAccounts() throws Exception {
        mockMvc.perform(get("/accounts").with(asCustomer("CUST-ALICE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.accountNumber=='ACC-1001')]").exists())
                .andExpect(jsonPath("$[?(@.accountNumber=='ACC-2001')]").doesNotExist());
    }

    @Test
    void aliceCanReadHerOwnAccountByNumber() throws Exception {
        mockMvc.perform(get("/accounts/ACC-1001").with(asCustomer("CUST-ALICE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(5000.00))
                .andExpect(jsonPath("$.currency").value("SGD"));
    }

    @Test
    void aliceCannotReadBobsAccount() throws Exception {
        mockMvc.perform(get("/accounts/ACC-2001").with(asCustomer("CUST-ALICE")))
                .andExpect(status().isNotFound());
    }

    @Test
    void anonymousIsRejected() throws Exception {
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isUnauthorized());
    }
}
