package sg.edu.rp.sandbox.compliance;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Runtime overrides for the simulated compliance providers, used for live demos.
 *
 * <p>An instructor can force the next KYC decision or temporarily change the AML
 * flag threshold without restarting anything. The admin endpoint added in a
 * later task writes here; the simulated providers read from here first.
 *
 * <p>Scoped strictly to the SIMULATED providers - it has no effect once a real
 * vendor adapter is plugged in.
 */
@Component
public class ComplianceOverrides {

    /** If set, the next KYC verification returns this status, then clears. */
    private final AtomicReference<String> forcedNextKycStatus = new AtomicReference<>();

    /** If set, overrides the configured AML flag threshold. */
    private final AtomicReference<BigDecimal> amlFlagThresholdOverride = new AtomicReference<>();

    public void forceNextKycStatus(String status) {
        forcedNextKycStatus.set(status);
    }

    /** Returns and clears the forced KYC status (one-shot). */
    public Optional<String> consumeForcedKycStatus() {
        return Optional.ofNullable(forcedNextKycStatus.getAndSet(null));
    }

    public void setAmlFlagThresholdOverride(BigDecimal threshold) {
        amlFlagThresholdOverride.set(threshold);
    }

    public Optional<BigDecimal> amlFlagThresholdOverride() {
        return Optional.ofNullable(amlFlagThresholdOverride.get());
    }

    public void clear() {
        forcedNextKycStatus.set(null);
        amlFlagThresholdOverride.set(null);
    }
}
