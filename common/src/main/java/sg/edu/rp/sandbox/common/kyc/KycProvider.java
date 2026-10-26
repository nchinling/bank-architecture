package sg.edu.rp.sandbox.common.kyc;

/**
 * Pluggable eKYC (electronic Know-Your-Customer) provider.
 *
 * <p>This is a detachable contract. In v1 the sandbox ships a simulated,
 * rule-based implementation. To engage a real vendor later, add a new
 * implementation of this interface and flip the {@code provider.kyc}
 * configuration property - no calling code changes.
 */
public interface KycProvider {

    /**
     * Runs a KYC check for the given applicant.
     *
     * @param request applicant details to screen
     * @return the screening outcome (never {@code null})
     */
    KycResult verify(KycRequest request);
}
