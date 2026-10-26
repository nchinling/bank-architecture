package sg.edu.rp.sandbox.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Identity configuration shared by all services.
 *
 * <pre>
 * provider:
 *   identity: keycloak      # or "builtin" later
 * sandbox:
 *   identity:
 *     issuer-uri: http://localhost:8080/realms/digital-bank
 *     customer-claim: customer_ref   # claim that carries the stable customer reference
 * </pre>
 */
@ConfigurationProperties(prefix = "sandbox.identity")
public class IdentityProperties {

    /** OIDC issuer URI used to validate JWTs (also drives JWK discovery). */
    private String issuerUri = "http://localhost:8080/realms/digital-bank";

    /** Token claim holding the stable customer reference. */
    private String customerClaim = "customer_ref";

    public String getIssuerUri() {
        return issuerUri;
    }

    public void setIssuerUri(String issuerUri) {
        this.issuerUri = issuerUri;
    }

    public String getCustomerClaim() {
        return customerClaim;
    }

    public void setCustomerClaim(String customerClaim) {
        this.customerClaim = customerClaim;
    }
}
