package sg.edu.rp.sandbox.customer;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Proves the resource-server security is wired correctly:
 * <ul>
 *   <li>an unauthenticated request to a protected endpoint is rejected (401),</li>
 *   <li>a request bearing a valid JWT reaches the endpoint.</li>
 * </ul>
 *
 * <p>Security is enabled for this test by setting the issuer-uri property (which
 * activates {@code ResourceServerSecurityConfig}). A mock {@link JwtDecoder}
 * replaces the real Keycloak call, so no live Keycloak is required.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/digital-bank"
})
class CustomerSecurityIntegrationTest {

    @TestConfiguration
    static class MockDecoderConfig {
        // Replaces the real JWK-based decoder so the context starts without Keycloak.
        @Bean
        JwtDecoder jwtDecoder() {
            return mock(JwtDecoder.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void protectedEndpointRejectsAnonymous() throws Exception {
        mockMvc.perform(get("/customers/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointAcceptsValidJwt() throws Exception {
        // A valid token whose customer_ref points at a non-existent customer still
        // proves authentication succeeded: we get past security to a 404, not a 401.
        mockMvc.perform(get("/customers/me")
                        .with(jwt().jwt(j -> j.subject("sub-1").claim("customer_ref", "CUST-does-not-exist"))))
                .andExpect(status().isNotFound());
    }
}
