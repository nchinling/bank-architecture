package sg.edu.rp.sandbox.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Converts a Keycloak JWT into a Spring {@link AbstractAuthenticationToken},
 * mapping Keycloak realm roles ({@code realm_access.roles}) to Spring authorities
 * with the {@code ROLE_} prefix (e.g. realm role "admin" -> authority "ROLE_admin").
 *
 * <p>Isolated here so the vendor-specific claim layout does not leak into services.
 */
public class KeycloakRealmRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    @SuppressWarnings("unchecked")
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof Map<?, ?> map) {
            Object roles = map.get("roles");
            if (roles instanceof List<?> roleList) {
                for (Object role : roleList) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }
        }
        return new JwtAuthenticationToken(jwt, authorities);
    }
}
