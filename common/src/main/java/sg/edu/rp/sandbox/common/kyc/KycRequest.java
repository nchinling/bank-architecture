package sg.edu.rp.sandbox.common.kyc;

/**
 * Input to a KYC screening. Kept vendor-neutral: only the fields any provider
 * (simulated or real) would reasonably need.
 *
 * @param fullName   applicant's full legal name
 * @param email      applicant's email
 * @param nationalId applicant's national identity reference (may be null in the sandbox)
 */
public record KycRequest(String fullName, String email, String nationalId) {
}
