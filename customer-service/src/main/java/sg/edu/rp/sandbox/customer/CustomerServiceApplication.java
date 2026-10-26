package sg.edu.rp.sandbox.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import sg.edu.rp.sandbox.security.ResourceServerSecurityConfig;

/**
 * Customer Service - onboarding and simulated eKYC.
 *
 * <p>Imports the shared resource-server security so this service validates JWTs
 * consistently with the rest of the platform. Defining it as a user bean (not an
 * auto-config import) lets Spring Boot back off its default OAuth2 chain cleanly.
 */
@SpringBootApplication
@Import(ResourceServerSecurityConfig.class)
public class CustomerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
