package sg.edu.rp.sandbox.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Immutable ledger leg written as part of a transfer. The two legs of a transfer
 * share one {@code transactionRef}.
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

    @Column(nullable = false, updatable = false)
    private String direction;

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
    }

    public LedgerEntry(String transactionRef, String accountNumber, String customerRef,
                       String direction, BigDecimal amount, String currency,
                       String description, String counterparty) {
        this.transactionRef = transactionRef;
        this.accountNumber = accountNumber;
        this.customerRef = customerRef;
        this.direction = direction;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.counterparty = counterparty;
        this.bookedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getTransactionRef() {
        return transactionRef;
    }
}
