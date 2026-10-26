package sg.edu.rp.sandbox.payment.service;

import java.math.BigDecimal;

/**
 * Outcome of a transfer attempt.
 *
 * @param transactionRef reference shared by both ledger legs
 * @param status         COMPLETED, HELD (AML review) or BLOCKED (AML flagged)
 * @param fromAccount    source account
 * @param toAccount      destination account
 * @param amount         amount transferred
 * @param currency       ISO currency code
 * @param amlReason      explanation from the AML screen
 */
public record TransferResult(
        String transactionRef,
        String status,
        String fromAccount,
        String toAccount,
        BigDecimal amount,
        String currency,
        String amlReason) {
}
