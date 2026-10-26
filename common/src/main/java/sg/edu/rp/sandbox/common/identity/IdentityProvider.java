package sg.edu.rp.sandbox.common.identity;

import java.util.Optional;

/**
 * Pluggable identity provider (OAuth2 / OIDC).
 *
 * <p>Detachable contract and a confirmed hard requirement of this project:
 * v1 uses Keycloak (a {@code KeycloakIdentityProvider} selected by
 * {@code provider.identity=keycloak}). It MUST remain swappable for a built-in
 * minimal auth service ({@code provider.identity=builtin}) via configuration
 * alone, with no changes to the services that depend on this interface.
 *
 * <p>Services validate tokens via Spring Security's resource-server support;
 * this adapter exposes the provider-specific details (issuer, well-known
 * endpoints, and claim extraction) that would otherwise leak a vendor choice
 * into application code.
 */
public interface IdentityProvider {

    /** A short identifier of the active provider, e.g. "keycloak" or "builtin". */
    String name();

    /** The OIDC issuer URI used to validate JWTs. */
    String issuerUri();

    /**
     * Extracts the authenticated subject's stable customer reference from the
     * verified token claims.
     *
     * @param claims verified JWT claims
     * @return the customer reference if present
     */
    Optional<String> customerReference(TokenClaims claims);
}
