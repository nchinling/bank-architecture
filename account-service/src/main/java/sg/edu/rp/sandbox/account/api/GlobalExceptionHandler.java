package sg.edu.rp.sandbox.account.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sg.edu.rp.sandbox.common.error.ApiError;
import sg.edu.rp.sandbox.common.error.BusinessException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest req) {
        HttpStatus status = switch (ex.getErrorCode()) {
            case "ACCOUNT_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "NO_CUSTOMER_REF" -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.BAD_REQUEST;
        };
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(), status.value(), ex.getErrorCode(), ex.getMessage(), req.getRequestURI()));
    }
}
