package sg.edu.rp.sandbox.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Shared Spring Security resource-server configuration.
 *
 * <p>Validates incoming JWTs against the configured OIDC issuer (Keycloak by
 * default) and maps realm roles to authorities. Stateless (no sessions).
 *
 * <p>Public by design: actuator health, OpenAPI docs, and Swagger UI. Everything
 * else requires a valid bearer token. Because services talk to the
 * {@link sg.edu.rp.sandbox.common.identity.IdentityProvider} contract for claim
 * extraction, swapping the identity vendor needs no change here beyond the
 * issuer URI.
 */
@Configuration
@EnableMethodSecurity
@ConditionalOnProperty(name = "spring.security.oauth2.resourceserver.jwt.issuer-uri")
public class ResourceServerSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/health/**",
                                "/actuator/info",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()
                        // Allow unauthenticated preflight requests for browser clients.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakRealmRoleConverter())));
        return http.build();
    }
}
