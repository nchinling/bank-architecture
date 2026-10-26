package sg.edu.rp.sandbox.compliance;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import sg.edu.rp.sandbox.common.kyc.KycProvider;
import sg.edu.rp.sandbox.common.kyc.KycRequest;
import sg.edu.rp.sandbox.common.kyc.KycResult;
import sg.edu.rp.sandbox.common.kyc.KycStatus;

import java.util.Locale;

/**
 * Deterministic, rule-based KYC simulation.
 *
 * <p>Teaching rules (checked against the applicant's full name, case-insensitive):
 * <ul>
 *   <li>contains "FRAUD"  -&gt; REJECTED</li>
 *   <li>contains "REVIEW" -&gt; MANUAL_REVIEW</li>
 *   <li>otherwise         -&gt; the configured default (APPROVED by default)</li>
 * </ul>
 *
 * <p>A one-shot runtime override (set by the instructor admin endpoint) takes
 * precedence over the rules, enabling live demos.
 *
 * <p>Selected when {@code provider.kyc=simulated} (the v1 default). A real vendor
 * implementation would be gated on {@code provider.kyc=vendor} instead.
 */
@Component
@ConditionalOnProperty(name = "provider.kyc", havingValue = "simulated", matchIfMissing = true)
public class SimulatedKycProvider implements KycProvider {

    private final ComplianceProperties properties;
    private final ComplianceOverrides overrides;

    public SimulatedKycProvider(ComplianceProperties properties, ComplianceOverrides overrides) {
        this.properties = properties;
        this.overrides = overrides;
    }

    @Override
    public KycResult verify(KycRequest request) {
        // 1) Runtime override wins (one-shot), for instructor-driven demos.
        var forced = overrides.consumeForcedKycStatus();
        if (forced.isPresent()) {
            KycStatus status = parse(forced.get());
            return new KycResult(status, "Forced by instructor override");
        }

        // 2) Deterministic name-based rules.
        String name = request.fullName() == null ? "" : request.fullName().toUpperCase(Locale.ROOT);
        if (name.contains("FRAUD")) {
            return KycResult.rejected("Name matched deterministic FRAUD rule");
        }
        if (name.contains("REVIEW")) {
            return KycResult.manualReview("Name matched deterministic REVIEW rule");
        }

        // 3) Configured default.
        KycStatus def = parse(properties.getKyc().getDefaultStatus());
        return new KycResult(def, "Default simulated KYC decision");
    }

    private KycStatus parse(String value) {
        try {
            return KycStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException ex) {
            return KycStatus.APPROVED;
        }
    }
}
