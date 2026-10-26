package sg.edu.rp.sandbox.common.kyc;

/**
 * Outcome of a KYC screening.
 *
 * @param status the decision
 * @param reason human-readable explanation (useful for teaching/demos)
 */
public record KycResult(KycStatus status, String reason) {

    public static KycResult approved(String reason) {
        return new KycResult(KycStatus.APPROVED, reason);
    }

    public static KycResult rejected(String reason) {
        return new KycResult(KycStatus.REJECTED, reason);
    }

    public static KycResult manualReview(String reason) {
        return new KycResult(KycStatus.MANUAL_REVIEW, reason);
    }
}
