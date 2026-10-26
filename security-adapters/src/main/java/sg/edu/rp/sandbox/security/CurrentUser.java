package sg.edu.rp.sandbox.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import sg.edu.rp.sandbox.common.identity.IdentityProvider;
import sg.edu.rp.sandbox.common.identity.TokenClaims;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Convenience accessor that turns the current Spring Security authentication
 * into vendor-neutral {@link TokenClaims} and resolves the customer reference
 * via the active {@link IdentityProvider}.
 *
 * <p>Services use this instead of reading JWT claims directly, so no service
 * depends on a specific identity vendor's token layout.
 */
public class CurrentUser {

    private final IdentityProvider identityProvider;

    public CurrentUser(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    /** The stable customer reference of the authenticated caller, if any. */
    public Optional<String> customerReference() {
        return claims().flatMap(identityProvider::customerReference);
    }

    /** Vendor-neutral view of the current token's claims. */
    public Optional<TokenClaims> claims() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            Set<String> roles = jwtAuth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            return Optional.of(new TokenClaims(jwt.getSubject(), roles, jwt.getClaims()));
        }
        return Optional.empty();
    }
}
