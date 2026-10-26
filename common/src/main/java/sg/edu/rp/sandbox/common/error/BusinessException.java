package sg.edu.rp.sandbox.common.error;

/**
 * Base type for domain/business rule violations (e.g. insufficient funds,
 * account not found). Services map these to appropriate HTTP responses.
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
