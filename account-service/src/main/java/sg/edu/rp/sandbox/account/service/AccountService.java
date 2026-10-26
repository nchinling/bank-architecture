package sg.edu.rp.sandbox.account.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sg.edu.rp.sandbox.account.domain.Account;
import sg.edu.rp.sandbox.account.domain.AccountRepository;
import sg.edu.rp.sandbox.common.error.BusinessException;

import java.util.List;

/**
 * Account queries. All access is scoped to the owning customer; a caller can
 * only see accounts whose {@code customerRef} matches their authenticated token.
 */
@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Account> listForCustomer(String customerRef) {
        return repository.findByCustomerRef(customerRef);
    }

    /**
     * Returns an account by number, but only if it belongs to the given customer.
     * Treats "not yours" the same as "not found" to avoid leaking existence.
     */
    @Transactional(readOnly = true)
    public Account getOwnedAccount(String customerRef, String accountNumber) {
        return repository.findByAccountNumber(accountNumber)
                .filter(a -> a.getCustomerRef().equals(customerRef))
                .orElseThrow(() -> new BusinessException("ACCOUNT_NOT_FOUND",
                        "No account " + accountNumber + " for the current customer"));
    }
}
