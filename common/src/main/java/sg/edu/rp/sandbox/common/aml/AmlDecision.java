package sg.edu.rp.sandbox.common.aml;

/**
 * Possible AML/fraud decisions.
 */
public enum AmlDecision {
    /** Transaction is clear to proceed. */
    CLEAR,
    /** Transaction is held for manual review (not blocked outright). */
    REVIEW,
    /** Transaction is blocked. */
    FLAGGED
}
