package sg.edu.rp.sandbox.payment.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sg.edu.rp.sandbox.compliance.ComplianceOverrides;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Instructor-only admin endpoints to steer the SIMULATED AML behaviour for live
 * demos. Requires the {@code admin} realm role. Has no effect once a real vendor
 * AML adapter is plugged in.
 */
@RestController
@RequestMapping("/admin/aml")
@PreAuthorize("hasRole('admin')")
@SecurityRequirement(name = "bearer-jwt")
public class AdminController {

    private final ComplianceOverrides overrides;

    public AdminController(ComplianceOverrides overrides) {
        this.overrides = overrides;
    }

    @Operation(summary = "Override the AML flag threshold",
            description = "Sets the amount at or above which a transfer is FLAGGED (blocked). "
                    + "Lower it to demo blocking on small transfers. Admin role required.")
    @PostMapping("/flag-threshold")
    public Map<String, Object> setFlagThreshold(@RequestParam BigDecimal amount) {
        overrides.setAmlFlagThresholdOverride(amount);
        return Map.of("amlFlagThresholdOverride", amount);
    }

    @Operation(summary = "Clear compliance overrides",
            description = "Clears any AML threshold override and forced KYC outcome. Admin role required.")
    @PostMapping("/reset")
    public Map<String, String> reset() {
        overrides.clear();
        return Map.of("status", "cleared");
    }
}
