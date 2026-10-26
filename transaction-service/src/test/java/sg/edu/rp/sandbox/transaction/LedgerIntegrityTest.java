package sg.edu.rp.sandbox.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import sg.edu.rp.sandbox.transaction.domain.Direction;
import sg.edu.rp.sandbox.transaction.domain.LedgerEntry;
import sg.edu.rp.sandbox.transaction.domain.LedgerEntryRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ledger integrity: the two legs of a transfer share one transaction_ref, are
 * balanced (equal amounts, opposite directions), and history returns legs newest
 * first. Uses the Flyway-migrated H2 schema (so seed data is present too).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LedgerIntegrityTest {

    @Autowired
    private LedgerEntryRepository repository;

    @Test
    void transferWritesTwoBalancedLegsSharingATransactionRef() {
        String ref = "TXN-TEST-1";
        Instant now = Instant.now();
        repository.saveAll(List.of(
                new LedgerEntry(ref, "ACC-1001", "CUST-ALICE", Direction.DEBIT,
                        new BigDecimal("100.00"), "SGD", "to Bob", "ACC-2001", now),
                new LedgerEntry(ref, "ACC-2001", "CUST-BOB", Direction.CREDIT,
                        new BigDecimal("100.00"), "SGD", "from Alice", "ACC-1001", now)));

        List<LedgerEntry> alice = repository.findHistory("ACC-1001", "CUST-ALICE", null, null);
        List<LedgerEntry> bob = repository.findHistory("ACC-2001", "CUST-BOB", null, null);

        assertThat(alice).anyMatch(e -> e.getTransactionRef().equals(ref) && e.getDirection() == Direction.DEBIT);
        assertThat(bob).anyMatch(e -> e.getTransactionRef().equals(ref) && e.getDirection() == Direction.CREDIT);

        BigDecimal debit = alice.stream().filter(e -> e.getTransactionRef().equals(ref))
                .map(LedgerEntry::getAmount).findFirst().orElseThrow();
        BigDecimal credit = bob.stream().filter(e -> e.getTransactionRef().equals(ref))
                .map(LedgerEntry::getAmount).findFirst().orElseThrow();
        assertThat(debit).isEqualByComparingTo(credit);
    }

    @Test
    void historyIsOrderedNewestFirst() {
        List<LedgerEntry> history = repository.findHistory("ACC-1001", "CUST-ALICE", null, null);
        // Seeded: salary on 2026-09-25 should come before the 2026-09-01 transfer.
        assertThat(history).isNotEmpty();
        for (int i = 1; i < history.size(); i++) {
            assertThat(history.get(i - 1).getBookedAt())
                    .isAfterOrEqualTo(history.get(i).getBookedAt());
        }
    }
}
