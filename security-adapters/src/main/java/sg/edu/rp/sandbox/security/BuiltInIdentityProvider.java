package sg.edu.rp.sandbox.security;

import sg.edu.rp.sandbox.common.identity.IdentityProvider;
import sg.edu.rp.sandbox.common.identity.TokenClaims;

import java.util.Optional;

/**
 * Built-in minimal identity provider.
 *
 * <p>This exists to prove - and preserve - the confirmed requirement that
 * Keycloak can be swapped out for an in-house auth service via configuration
 * alone. Registered as a bean when {@code provider.identity=builtin}. In v1 it
 * simply reads the customer reference from the subject; a later task could back
 * it with a small Spring-based JWT issuer. No service code changes when switching.
 */
public class BuiltInIdentityProvider implements IdentityProvider {

    private final IdentityProperties properties;

    public BuiltInIdentityProvider(IdentityProperties properties) {
        this.properties = properties;
    }

    @Override
    public String name() {
        return "builtin";
    }

    @Override
    public String issuerUri() {
        return properties.getIssuerUri();
    }

    @Override
    public Optional<String> customerReference(TokenClaims claims) {
        if (claims == null) {
            return Optional.empty();
        }
        Object value = claims.raw() == null ? null : claims.raw().get(properties.getCustomerClaim());
        if (value != null) {
            return Optional.of(value.toString());
        }
        return Optional.ofNullable(claims.subject());
    }
}
