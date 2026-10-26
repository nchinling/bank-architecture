package sg.edu.rp.sandbox.customer.api;

import sg.edu.rp.sandbox.common.kyc.KycStatus;
import sg.edu.rp.sandbox.customer.domain.Customer;

import java.time.Instant;

/**
 * Public view of a customer returned by the API.
 */
public record CustomerResponse(
        String customerRef,
        String fullName,
        String email,
        String nationalId,
        KycStatus kycStatus,
        String kycReason,
        Instant createdAt) {

    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(
                c.getCustomerRef(), c.getFullName(), c.getEmail(), c.getNationalId(),
                c.getKycStatus(), c.getKycReason(), c.getCreatedAt());
    }
}
