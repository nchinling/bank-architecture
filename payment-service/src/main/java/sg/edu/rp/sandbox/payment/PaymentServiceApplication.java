package sg.edu.rp.sandbox.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import sg.edu.rp.sandbox.security.ResourceServerSecurityConfig;

/**
 * Payment Service - internal transfers with AML screening and events.
 *
 * <p>For v1 the service owns its own accounts and ledger tables so a transfer is
 * a single atomic local transaction (InnoDB). It imports the shared
 * resource-server security so transfers are scoped to the authenticated customer.
 */
@SpringBootApplication
@Import(ResourceServerSecurityConfig.class)
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
