package sg.edu.rp.sandbox.common.event;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Published when an internal transfer completes successfully.
 *
 * @param transactionRef the transfer's reference (shared by both ledger legs)
 * @param fromAccount    source account number
 * @param toAccount      destination account number
 * @param amount         transfer amount
 * @param currency       ISO currency code
 * @param occurredAt     completion time
 */
public record PaymentCompletedEvent(
        String transactionRef,
        String fromAccount,
        String toAccount,
        BigDecimal amount,
        String currency,
        Instant occurredAt) implements DomainEvent {

    @Override
    public String eventType() {
        return "PaymentCompleted";
    }
}
