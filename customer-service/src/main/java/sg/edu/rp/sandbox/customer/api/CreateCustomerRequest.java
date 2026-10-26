package sg.edu.rp.sandbox.customer.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * Request body for creating a customer. Core identity fields are required;
 * profile fields are optional.
 */
public record CreateCustomerRequest(
        @NotBlank(message = "fullName is required") String fullName,
        @NotBlank(message = "email is required") @Email(message = "email must be valid") String email,
        String nationalId,
        // Optional profile
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
