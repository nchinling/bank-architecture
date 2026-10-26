package sg.edu.rp.sandbox.account.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByCustomerRef(String customerRef);

    Optional<Account> findByAccountNumber(String accountNumber);
}
