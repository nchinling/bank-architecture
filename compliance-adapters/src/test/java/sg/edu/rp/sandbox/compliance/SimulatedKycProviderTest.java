package sg.edu.rp.sandbox.compliance;

import org.junit.jupiter.api.Test;
import sg.edu.rp.sandbox.common.kyc.KycRequest;
import sg.edu.rp.sandbox.common.kyc.KycResult;
import sg.edu.rp.sandbox.common.kyc.KycStatus;

import static org.assertj.core.api.Assertions.assertThat;

class SimulatedKycProviderTest {

    private final ComplianceProperties properties = new ComplianceProperties();
    private final ComplianceOverrides overrides = new ComplianceOverrides();
    private final SimulatedKycProvider provider = new SimulatedKycProvider(properties, overrides);

    @Test
    void approvesOrdinaryName() {
        KycResult result = provider.verify(new KycRequest("Alice Tan", "alice@example.com", "S1234567A"));
        assertThat(result.status()).isEqualTo(KycStatus.APPROVED);
    }

    @Test
    void rejectsNameContainingFraud() {
        KycResult result = provider.verify(new KycRequest("Fraud Test", "f@example.com", null));
        assertThat(result.status()).isEqualTo(KycStatus.REJECTED);
    }

    @Test
    void sendsNameContainingReviewToManualReview() {
        KycResult result = provider.verify(new KycRequest("Review Me", "r@example.com", null));
        assertThat(result.status()).isEqualTo(KycStatus.MANUAL_REVIEW);
    }

    @Test
    void ruleMatchingIsCaseInsensitive() {
        assertThat(provider.verify(new KycRequest("john fRaUd", "j@example.com", null)).status())
                .isEqualTo(KycStatus.REJECTED);
    }

    @Test
    void forcedOverrideWinsAndIsOneShot() {
        overrides.forceNextKycStatus("REJECTED");
        // Even an ordinary name is rejected while the override is active.
        assertThat(provider.verify(new KycRequest("Alice Tan", "a@example.com", null)).status())
                .isEqualTo(KycStatus.REJECTED);
        // Override is consumed; the next call falls back to the rules/default.
        assertThat(provider.verify(new KycRequest("Alice Tan", "a@example.com", null)).status())
                .isEqualTo(KycStatus.APPROVED);
    }
}
