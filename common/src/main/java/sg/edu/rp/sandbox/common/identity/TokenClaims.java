package sg.edu.rp.sandbox.common.identity;

import java.util.Map;
import java.util.Set;

/**
 * Vendor-neutral view of verified token claims, so services never depend on a
 * specific identity provider's claim layout.
 *
 * @param subject the token subject (sub)
 * @param roles   granted roles/authorities
 * @param raw     all raw claims, for providers that expose extra fields
 */
public record TokenClaims(String subject, Set<String> roles, Map<String, Object> raw) {
}
