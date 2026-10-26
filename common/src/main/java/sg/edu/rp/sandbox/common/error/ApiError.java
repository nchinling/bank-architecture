package sg.edu.rp.sandbox.common.error;

import java.time.Instant;

/**
 * Standard error body returned by all services, so clients (in any language)
 * see a consistent shape.
 *
 * @param timestamp when the error occurred
 * @param status    HTTP status code
 * @param error     short error code, e.g. "VALIDATION_FAILED"
 * @param message   human-readable detail
 * @param path      request path
 */
public record ApiError(Instant timestamp, int status, String error, String message, String path) {
}
