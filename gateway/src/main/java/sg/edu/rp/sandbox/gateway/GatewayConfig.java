package sg.edu.rp.sandbox.gateway;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the gateway's edge concerns: the API-key + rate-limit global filter.
 */
@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class GatewayConfig {

    @Bean
    ApiKeyRateLimitFilter apiKeyRateLimitFilter(GatewayProperties properties) {
        return new ApiKeyRateLimitFilter(properties);
    }
}
