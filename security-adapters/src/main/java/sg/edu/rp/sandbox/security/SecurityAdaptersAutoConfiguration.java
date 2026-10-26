package sg.edu.rp.sandbox.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sg.edu.rp.sandbox.common.identity.IdentityProvider;

/**
 * Auto-configuration that wires the identity adapters, the shared resource-server
 * security, and the {@link CurrentUser} helper into any service depending on this
 * module. Beans are declared explicitly (no component scanning) so the module
 * composes cleanly with each service's own configuration.
 */
@Configuration
@EnableConfigurationProperties(IdentityProperties.class)
public class SecurityAdaptersAutoConfiguration {

    /** v1 default identity provider: Keycloak. */
    @Bean
    @ConditionalOnProperty(name = "provider.identity", havingValue = "keycloak", matchIfMissing = true)
    IdentityProvider keycloakIdentityProvider(IdentityProperties properties) {
        return new KeycloakIdentityProvider(properties);
    }

    /** Opt-in built-in identity provider (proves swap-ability via config). */
    @Bean
    @ConditionalOnProperty(name = "provider.identity", havingValue = "builtin")
    IdentityProvider builtInIdentityProvider(IdentityProperties properties) {
        return new BuiltInIdentityProvider(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    CurrentUser currentUser(IdentityProvider identityProvider) {
        return new CurrentUser(identityProvider);
    }
}
