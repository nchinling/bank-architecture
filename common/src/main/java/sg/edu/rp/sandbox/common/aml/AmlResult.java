package sg.edu.rp.sandbox.common.aml;

/**
 * Outcome of an AML/fraud screening.
 *
 * @param decision the decision
 * @param reason   human-readable explanation (useful for teaching/demos)
 */
public record AmlResult(AmlDecision decision, String reason) {

    public static AmlResult clear(String reason) {
        return new AmlResult(AmlDecision.CLEAR, reason);
    }

    public static AmlResult review(String reason) {
        return new AmlResult(AmlDecision.REVIEW, reason);
    }

    public static AmlResult flagged(String reason) {
        return new AmlResult(AmlDecision.FLAGGED, reason);
    }
}
