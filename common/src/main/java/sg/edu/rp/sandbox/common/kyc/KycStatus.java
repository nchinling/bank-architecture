package sg.edu.rp.sandbox.common.kyc;

/**
 * Possible KYC decisions. Deterministic in the simulated provider so lessons
 * can reliably reproduce each outcome.
 */
public enum KycStatus {
    /** Applicant passed screening. */
    APPROVED,
    /** Applicant failed screening outright. */
    REJECTED,
    /** Applicant needs a human reviewer (e.g. a compliance officer). */
    MANUAL_REVIEW
}
