package sg.edu.rp.sandbox.account.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sg.edu.rp.sandbox.account.service.AccountService;
import sg.edu.rp.sandbox.common.error.BusinessException;
import sg.edu.rp.sandbox.security.CurrentUser;

import java.util.List;

/**
 * Account endpoints. The owning customer is always taken from the authenticated
 * token (never from the request), so callers can only ever see their own accounts.
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;
    private final CurrentUser currentUser;

    public AccountController(AccountService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    /** Lists the authenticated customer's accounts. */
    @GetMapping
    public List<AccountResponse> listMyAccounts() {
        String ref = requireCustomerRef();
        return service.listForCustomer(ref).stream().map(AccountResponse::from).toList();
    }

    /** Returns one of the authenticated customer's accounts by number. */
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getMyAccount(@PathVariable String accountNumber) {
        String ref = requireCustomerRef();
        return ResponseEntity.ok(AccountResponse.from(service.getOwnedAccount(ref, accountNumber)));
    }

    private String requireCustomerRef() {
        return currentUser.customerReference()
                .orElseThrow(() -> new BusinessException("NO_CUSTOMER_REF",
                        "Authenticated token has no customer reference"));
    }
}
