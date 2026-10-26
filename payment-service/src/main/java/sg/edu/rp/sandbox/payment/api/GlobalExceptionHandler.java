package sg.edu.rp.sandbox.payment.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sg.edu.rp.sandbox.common.error.ApiError;
import sg.edu.rp.sandbox.common.error.BusinessException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest req) {
        HttpStatus status = switch (ex.getErrorCode()) {
            case "ACCOUNT_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "NOT_ACCOUNT_OWNER", "NO_CUSTOMER_REF" -> HttpStatus.FORBIDDEN;
            case "AML_BLOCKED" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "AML_HELD" -> HttpStatus.ACCEPTED;
            // INSUFFICIENT_FUNDS, SAME_ACCOUNT, CURRENCY_MISMATCH, INVALID_AMOUNT
            default -> HttpStatus.CONFLICT;
        };
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(), status.value(), ex.getErrorCode(), ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(new ApiError(
                Instant.now(), HttpStatus.BAD_REQUEST.value(), "VALIDATION_FAILED", message, req.getRequestURI()));
    }
}
