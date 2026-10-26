package sg.edu.rp.sandbox.customer.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerRef(String customerRef);

    boolean existsByEmail(String email);
}
