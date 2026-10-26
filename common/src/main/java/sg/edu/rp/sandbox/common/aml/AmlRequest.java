package sg.edu.rp.sandbox.common.aml;

import java.math.BigDecimal;

/**
 * Input to an AML/fraud screening.
 *
 * @param fromAccount source account identifier
 * @param toAccount   destination account identifier
 * @param amount      transaction amount (always use {@link BigDecimal} for money)
 * @param currency    ISO currency code, e.g. "SGD"
 */
public record AmlRequest(String fromAccount, String toAccount, BigDecimal amount, String currency) {
}
