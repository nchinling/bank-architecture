package sg.edu.rp.sandbox.compliance;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import sg.edu.rp.sandbox.common.aml.AmlProvider;
import sg.edu.rp.sandbox.common.aml.AmlRequest;
import sg.edu.rp.sandbox.common.aml.AmlResult;

import java.math.BigDecimal;

/**
 * Deterministic, threshold-based AML simulation.
 *
 * <p>Teaching rules (based on the transfer amount):
 * <ul>
 *   <li>amount &gt;= flag threshold   -&gt; FLAGGED (blocked)</li>
 *   <li>amount &gt;= review threshold -&gt; REVIEW (held)</li>
 *   <li>otherwise                     -&gt; CLEAR</li>
 * </ul>
 *
 * <p>The flag threshold can be overridden at runtime by the instructor admin
 * endpoint, otherwise the configured value is used.
 *
 * <p>Selected when {@code provider.aml=simulated} (the v1 default).
 */
@Component
@ConditionalOnProperty(name = "provider.aml", havingValue = "simulated", matchIfMissing = true)
public class SimulatedAmlProvider implements AmlProvider {

    private final ComplianceProperties properties;
    private final ComplianceOverrides overrides;

    public SimulatedAmlProvider(ComplianceProperties properties, ComplianceOverrides overrides) {
        this.properties = properties;
        this.overrides = overrides;
    }

    @Override
    public AmlResult screen(AmlRequest request) {
        BigDecimal amount = request.amount() == null ? BigDecimal.ZERO : request.amount();

        BigDecimal flagThreshold = overrides.amlFlagThresholdOverride()
                .orElse(properties.getAml().getFlagThreshold());
        BigDecimal reviewThreshold = properties.getAml().getReviewThreshold();

        if (amount.compareTo(flagThreshold) >= 0) {
            return AmlResult.flagged("Amount " + amount + " >= flag threshold " + flagThreshold);
        }
        if (amount.compareTo(reviewThreshold) >= 0) {
            return AmlResult.review("Amount " + amount + " >= review threshold " + reviewThreshold);
        }
        return AmlResult.clear("Amount " + amount + " below review threshold " + reviewThreshold);
    }
}
