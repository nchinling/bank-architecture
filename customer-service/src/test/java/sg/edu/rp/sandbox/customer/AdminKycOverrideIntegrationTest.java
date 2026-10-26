package sg.edu.rp.sandbox.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies the instructor admin endpoint:
 * <ul>
 *   <li>an admin can force the next KYC outcome, which then applies to a create,</li>
 *   <li>a non-admin is forbidden (403),</li>
 *   <li>an anonymous caller is unauthorized (401).</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/digital-bank"
})
class AdminKycOverrideIntegrationTest {

    @TestConfiguration
    static class MockDecoderConfig {
        @Bean
        JwtDecoder jwtDecoder() {
            return mock(JwtDecoder.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminCanForceNextKycRejection() throws Exception {
        // Admin forces the next KYC to REJECTED.
        mockMvc.perform(post("/admin/kyc/force-next").param("status", "REJECTED")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_admin"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.forcedNextKycStatus").value("REJECTED"));

        // The next ordinary customer creation is now REJECTED despite a clean name.
        String body = """
            {"fullName":"Clean Name","email":"clean@example.com"}
            """;
        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(body)
                        .with(jwt().jwt(j -> j.claim("customer_ref", "CUST-X"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.kycStatus").value("REJECTED"));
    }

    @Test
    void nonAdminIsForbidden() throws Exception {
        mockMvc.perform(post("/admin/kyc/force-next").param("status", "REJECTED")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_customer"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void anonymousIsUnauthorized() throws Exception {
        mockMvc.perform(post("/admin/kyc/force-next").param("status", "REJECTED"))
                .andExpect(status().isUnauthorized());
    }
}
