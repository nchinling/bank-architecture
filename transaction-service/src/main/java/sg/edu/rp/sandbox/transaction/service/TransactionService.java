package sg.edu.rp.sandbox.transaction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sg.edu.rp.sandbox.transaction.domain.LedgerEntry;
import sg.edu.rp.sandbox.transaction.domain.LedgerEntryRepository;

import java.time.Instant;
import java.util.List;

/**
 * Append-only ledger operations: record new legs and read history.
 * Entries are never updated or deleted.
 */
@Service
public class TransactionService {

    private final LedgerEntryRepository repository;

    public TransactionService(LedgerEntryRepository repository) {
        this.repository = repository;
    }

    /** Appends one or more ledger legs (e.g. the two legs of a transfer). */
    @Transactional
    public List<LedgerEntry> record(List<LedgerEntry> entries) {
        return repository.saveAll(entries);
    }

    /** Statement-style history for an account, scoped to its owning customer. */
    @Transactional(readOnly = true)
    public List<LedgerEntry> history(String accountNumber, String customerRef, Instant from, Instant to) {
        return repository.findHistory(accountNumber, customerRef, from, to);
    }
}
