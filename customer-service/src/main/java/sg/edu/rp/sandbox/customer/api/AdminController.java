package sg.edu.rp.sandbox.customer.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sg.edu.rp.sandbox.compliance.ComplianceOverrides;

import java.util.Map;

/**
 * Instructor-only admin endpoints to steer the SIMULATED eKYC outcome for live
 * demos. Requires the {@code admin} realm role. Has no effect once a real vendor
 * KYC adapter is plugged in.
 */
@RestController
@RequestMapping("/admin/kyc")
@PreAuthorize("hasRole('admin')")
@SecurityRequirement(name = "bearer-jwt")
public class AdminController {

    private final ComplianceOverrides overrides;

    public AdminController(ComplianceOverrides overrides) {
        this.overrides = overrides;
    }

    @Operation(summary = "Force the next KYC outcome (one-shot)",
            description = "Forces the next customer creation's KYC result. Valid: APPROVED, REJECTED, MANUAL_REVIEW. "
                    + "Consumed by the next create, then cleared. Admin role required.")
    @PostMapping("/force-next")
    public Map<String, String> forceNext(@RequestParam String status) {
        overrides.forceNextKycStatus(status);
        return Map.of("forcedNextKycStatus", status);
    }

    @Operation(summary = "Clear compliance overrides",
            description = "Clears any forced KYC outcome and AML threshold override. Admin role required.")
    @PostMapping("/reset")
    public Map<String, String> reset() {
        overrides.clear();
        return Map.of("status", "cleared");
    }
}
