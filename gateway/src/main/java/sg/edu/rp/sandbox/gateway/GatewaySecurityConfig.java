package sg.edu.rp.sandbox.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Reactive (WebFlux) edge security for the gateway: validate JWTs at the single
 * entry point. Public paths (actuator health, aggregated OpenAPI/Swagger) stay
 * open; everything else requires a valid bearer token.
 *
 * <p>CORS is enabled for the browser-based client origins (the reference app and
 * common dev-server ports) so a browser app running on a different origin can call
 * the gateway. Pre-flight OPTIONS requests are permitted without authentication,
 * as required by the CORS protocol.
 *
 * <p>Conditional on the issuer-uri being configured so the gateway can start
 * (and tests can run) without a live Keycloak. This is the WebFlux counterpart
 * of the servlet {@code ResourceServerSecurityConfig} used by the services.
 */
@Configuration
@EnableWebFluxSecurity
@ConditionalOnProperty(name = "spring.security.oauth2.resourceserver.jwt.issuer-uri")
public class GatewaySecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(ex -> ex
                        // CORS pre-flight must be allowed through without a token.
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/actuator/health/**", "/actuator/info",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**")
                        .permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                }));
        return http.build();
    }

    /**
     * Allows browser clients (the reference app on :3000, Vite on :5173) to call
     * the gateway. Adjust the allowed origins here if you serve your app elsewhere.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
