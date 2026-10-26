package sg.edu.rp.sandbox.customer.api;

import sg.edu.rp.sandbox.common.kyc.KycStatus;
import sg.edu.rp.sandbox.customer.domain.Customer;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Public view of a customer returned by the API, including the optional profile.
 */
public record CustomerResponse(
        String customerRef,
        String fullName,
        String email,
        String nationalId,
        KycStatus kycStatus,
        String kycReason,
        Instant createdAt,
        Profile profile) {

    /** Optional profile details grouped into a nested object for a tidy JSON shape. */
    public record Profile(
            String phone,
            String addressLine1,
            String addressLine2,
            String city,
            String postalCode,
            String country,
            LocalDate dateOfBirth,
            String occupation,
            String preferredChannel) {
    }

    public static CustomerResponse from(Customer c) {
        Profile profile = new Profile(
                c.getPhone(), c.getAddressLine1(), c.getAddressLine2(), c.getCity(),
                c.getPostalCode(), c.getCountry(), c.getDateOfBirth(), c.getOccupation(),
                c.getPreferredChannel());
        return new CustomerResponse(
                c.getCustomerRef(), c.getFullName(), c.getEmail(), c.getNationalId(),
                c.getKycStatus(), c.getKycReason(), c.getCreatedAt(), profile);
    }
}
