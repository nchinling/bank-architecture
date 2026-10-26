package sg.edu.rp.sandbox.transaction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A single immutable ledger leg (DEBIT or CREDIT) against one account.
 * The two legs of a transfer share the same {@code transactionRef}.
 * Append-only: once written, an entry is never modified.
 */
@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_ref", nullable = false, updatable = false)
    private String transactionRef;

    @Column(name = "account_number", nullable = false, updatable = false)
    private String accountNumber;

    @Column(name = "customer_ref", nullable = false, updatable = false)
    private String customerRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private Direction direction;

    @Column(nullable = false, updatable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false)
    private String currency;

    @Column(updatable = false)
    private String description;

    @Column(updatable = false)
    private String counterparty;

    @Column(name = "booked_at", nullable = false, updatable = false)
    private Instant bookedAt;

    protected LedgerEntry() {
        // for JPA
    }

    public LedgerEntry(String transactionRef, String accountNumber, String customerRef,
                       Direction direction, BigDecimal amount, String currency,
                       String description, String counterparty, Instant bookedAt) {
        this.transactionRef = transactionRef;
        this.accountNumber = accountNumber;
        this.customerRef = customerRef;
        this.direction = direction;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.counterparty = counterparty;
        this.bookedAt = bookedAt == null ? Instant.now() : bookedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerRef() {
        return customerRef;
    }

    public Direction getDirection() {
        return direction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public Instant getBookedAt() {
        return bookedAt;
    }
}
