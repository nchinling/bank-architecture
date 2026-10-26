package sg.edu.rp.sandbox.account.api;

import sg.edu.rp.sandbox.account.domain.Account;

import java.math.BigDecimal;

/**
 * Public view of an account.
 */
public record AccountResponse(
        String accountNumber,
        String accountType,
        String currency,
        BigDecimal balance,
        String status) {

    public static AccountResponse from(Account a) {
        return new AccountResponse(
                a.getAccountNumber(), a.getAccountType(), a.getCurrency(), a.getBalance(), a.getStatus());
    }
}
