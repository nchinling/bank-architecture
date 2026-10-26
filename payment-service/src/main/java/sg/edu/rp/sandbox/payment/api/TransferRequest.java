package sg.edu.rp.sandbox.payment.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request body for an internal transfer. The source account owner is taken from
 * the authenticated token, never from the request.
 */
public record TransferRequest(
        @NotBlank(message = "fromAccount is required") String fromAccount,
        @NotBlank(message = "toAccount is required") String toAccount,
        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be positive") BigDecimal amount,
        String description) {
}
