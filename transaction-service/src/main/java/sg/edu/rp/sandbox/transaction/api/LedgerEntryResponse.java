package sg.edu.rp.sandbox.transaction.api;

import sg.edu.rp.sandbox.transaction.domain.Direction;
import sg.edu.rp.sandbox.transaction.domain.LedgerEntry;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Public view of a ledger entry (one statement line).
 */
public record LedgerEntryResponse(
        String transactionRef,
        String accountNumber,
        Direction direction,
        BigDecimal amount,
        String currency,
        String description,
        String counterparty,
        Instant bookedAt) {

    public static LedgerEntryResponse from(LedgerEntry e) {
        return new LedgerEntryResponse(
                e.getTransactionRef(), e.getAccountNumber(), e.getDirection(), e.getAmount(),
                e.getCurrency(), e.getDescription(), e.getCounterparty(), e.getBookedAt());
    }
}
