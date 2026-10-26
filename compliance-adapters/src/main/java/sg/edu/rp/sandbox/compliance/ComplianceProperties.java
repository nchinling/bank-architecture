package sg.edu.rp.sandbox.compliance;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/**
 * Configuration for the simulated compliance providers.
 *
 * <p>These give instructors deterministic-but-configurable behaviour:
 * sensible fixed defaults for lessons, with the ability to tune thresholds
 * via config (and, at runtime, via the admin endpoint added in a later task).
 *
 * <pre>
 * sandbox:
 *   compliance:
 *     kyc:
 *       default-status: APPROVED
 *     aml:
 *       flag-threshold: 10000.00
 *       currency: SGD
 * </pre>
 */
@ConfigurationProperties(prefix = "sandbox.compliance")
public class ComplianceProperties {

    private final Kyc kyc = new Kyc();
    private final Aml aml = new Aml();

    public Kyc getKyc() {
        return kyc;
    }

    public Aml getAml() {
        return aml;
    }

    public static class Kyc {
        /** Decision used when no deterministic name-based rule matches. */
        private String defaultStatus = "APPROVED";

        public String getDefaultStatus() {
            return defaultStatus;
        }

        public void setDefaultStatus(String defaultStatus) {
            this.defaultStatus = defaultStatus;
        }
    }

    public static class Aml {
        /** Transfers at or above this amount are flagged by the simulated rule. */
        private BigDecimal flagThreshold = new BigDecimal("10000.00");

        /** Transfers at or above this amount (but below flag) are sent to review. */
        private BigDecimal reviewThreshold = new BigDecimal("5000.00");

        public BigDecimal getFlagThreshold() {
            return flagThreshold;
        }

        public void setFlagThreshold(BigDecimal flagThreshold) {
            this.flagThreshold = flagThreshold;
        }

        public BigDecimal getReviewThreshold() {
            return reviewThreshold;
        }

        public void setReviewThreshold(BigDecimal reviewThreshold) {
            this.reviewThreshold = reviewThreshold;
        }
    }
}
