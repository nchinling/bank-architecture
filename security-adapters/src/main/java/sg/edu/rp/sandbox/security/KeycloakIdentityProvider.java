package sg.edu.rp.sandbox.security;

import sg.edu.rp.sandbox.common.identity.IdentityProvider;
import sg.edu.rp.sandbox.common.identity.TokenClaims;

import java.util.Optional;

/**
 * Keycloak-backed identity provider (the v1 default).
 *
 * <p>Registered as a bean when {@code provider.identity=keycloak}. It exposes the
 * issuer used for JWT validation and knows how to read the customer reference
 * from the configured claim. All provider-specific knowledge lives here, so
 * services depend only on the {@link IdentityProvider} contract.
 */
public class KeycloakIdentityProvider implements IdentityProvider {

    private final IdentityProperties properties;

    public KeycloakIdentityProvider(IdentityProperties properties) {
        this.properties = properties;
    }

    @Override
    public String name() {
        return "keycloak";
    }

    @Override
    public String issuerUri() {
        return properties.getIssuerUri();
    }

    @Override
    public Optional<String> customerReference(TokenClaims claims) {
        if (claims == null || claims.raw() == null) {
            return Optional.empty();
        }
        Object value = claims.raw().get(properties.getCustomerClaim());
        if (value != null) {
            return Optional.of(value.toString());
        }
        // Fallback: use the subject if no explicit customer claim is present.
        return Optional.ofNullable(claims.subject());
    }
}
