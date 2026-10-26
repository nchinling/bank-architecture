package sg.edu.rp.sandbox.transaction.api;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sg.edu.rp.sandbox.common.error.BusinessException;
import sg.edu.rp.sandbox.security.CurrentUser;
import sg.edu.rp.sandbox.transaction.service.TransactionService;

import java.time.Instant;
import java.util.List;

/**
 * Transaction history endpoints.
 *
 * <p>History is always scoped to the authenticated customer: the ledger stores
 * the owning {@code customerRef} on every entry, and the query filters on both
 * the account number and the caller's customer reference, so a caller can only
 * ever see statements for their own accounts.
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;
    private final CurrentUser currentUser;

    public TransactionController(TransactionService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    /**
     * Statement-style history for one of the caller's accounts.
     *
     * @param accountNumber the account to list
     * @param from optional inclusive lower bound (ISO-8601 instant, e.g. 2026-09-01T00:00:00Z)
     * @param to   optional inclusive upper bound
     */
    @GetMapping("/accounts/{accountNumber}")
    public List<LedgerEntryResponse> history(
            @PathVariable String accountNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        String ref = currentUser.customerReference()
                .orElseThrow(() -> new BusinessException("NO_CUSTOMER_REF",
                        "Authenticated token has no customer reference"));

        return service.history(accountNumber, ref, from, to).stream()
                .map(LedgerEntryResponse::from)
                .toList();
    }
}
