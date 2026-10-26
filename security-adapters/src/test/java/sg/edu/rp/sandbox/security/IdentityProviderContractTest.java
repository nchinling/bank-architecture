package sg.edu.rp.sandbox.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import sg.edu.rp.sandbox.common.identity.IdentityProvider;
import sg.edu.rp.sandbox.common.identity.TokenClaims;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contract test guaranteeing swap-ability: every {@link IdentityProvider}
 * implementation must satisfy the same behavioural contract, so switching
 * {@code provider.identity} is safe and requires no service code changes.
 *
 * <p>If a vendor provider is added later, add it to {@link #providers()} and it
 * must pass unchanged.
 */
class IdentityProviderContractTest {

    static Stream<IdentityProvider> providers() {
        IdentityProperties props = new IdentityProperties();
        return Stream.of(
                new KeycloakIdentityProvider(props),
                new BuiltInIdentityProvider(props));
    }

    @ParameterizedTest
    @MethodSource("providers")
    void exposesANonBlankName(IdentityProvider provider) {
        assertThat(provider.name()).isNotBlank();
    }

    @ParameterizedTest
    @MethodSource("providers")
    void exposesAnIssuerUri(IdentityProvider provider) {
        assertThat(provider.issuerUri()).isNotBlank();
    }

    @ParameterizedTest
    @MethodSource("providers")
    void readsCustomerReferenceFromTheConfiguredClaim(IdentityProvider provider) {
        TokenClaims claims = new TokenClaims(
                "sub-123",
                Set.of("ROLE_customer"),
                Map.of("customer_ref", "CUST-abc"));
        assertThat(provider.customerReference(claims)).contains("CUST-abc");
    }

    @ParameterizedTest
    @MethodSource("providers")
    void fallsBackToSubjectWhenClaimAbsent(IdentityProvider provider) {
        TokenClaims claims = new TokenClaims("sub-123", Set.of(), Map.of());
        assertThat(provider.customerReference(claims)).contains("sub-123");
    }

    @Test
    void keycloakAndBuiltInAgreeOnTheSameToken() {
        IdentityProperties props = new IdentityProperties();
        IdentityProvider keycloak = new KeycloakIdentityProvider(props);
        IdentityProvider builtin = new BuiltInIdentityProvider(props);

        TokenClaims claims = new TokenClaims("sub-9", Set.of(), Map.of("customer_ref", "CUST-xyz"));

        // Swapping providers must not change the resolved customer reference.
        assertThat(keycloak.customerReference(claims))
                .isEqualTo(builtin.customerReference(claims));
    }
}
