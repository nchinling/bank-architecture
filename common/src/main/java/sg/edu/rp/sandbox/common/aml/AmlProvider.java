package sg.edu.rp.sandbox.common.aml;

/**
 * Pluggable AML / fraud screening provider.
 *
 * <p>Detachable contract: v1 ships a simulated, threshold-based implementation.
 * A real transaction-monitoring vendor can be added later behind this same
 * interface and selected via the {@code provider.aml} configuration property.
 */
public interface AmlProvider {

    /**
     * Screens a transaction (e.g. a transfer) for AML/fraud concerns.
     *
     * @param request the transaction to screen
     * @return the screening outcome (never {@code null})
     */
    AmlResult screen(AmlRequest request);
}
