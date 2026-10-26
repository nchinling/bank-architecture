package sg.edu.rp.sandbox.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import sg.edu.rp.sandbox.security.ResourceServerSecurityConfig;

/**
 * Transaction Service - append-only ledger and history.
 *
 * <p>Imports the shared resource-server security so transaction history is
 * scoped to the authenticated customer.
 */
@SpringBootApplication
@Import(ResourceServerSecurityConfig.class)
public class TransactionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }
}
