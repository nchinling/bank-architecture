package sg.edu.rp.sandbox.customer.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sg.edu.rp.sandbox.common.error.BusinessException;
import sg.edu.rp.sandbox.customer.domain.Customer;
import sg.edu.rp.sandbox.customer.service.CustomerService;
import sg.edu.rp.sandbox.security.CurrentUser;

/**
 * Customer onboarding endpoints.
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;
    private final CurrentUser currentUser;

    public CustomerController(CustomerService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    /**
     * Returns the authenticated caller's own customer record, resolved from the
     * token via the active {@link sg.edu.rp.sandbox.common.identity.IdentityProvider}.
     */
    @Operation(summary = "Get my customer record",
            description = "Returns the authenticated caller's own customer record, resolved from the JWT.")
    @GetMapping("/me")
    public ResponseEntity<CustomerResponse> me() {
        String ref = currentUser.customerReference()
                .orElseThrow(() -> new BusinessException("NO_CUSTOMER_REF",
                        "Authenticated token has no customer reference"));
        return ResponseEntity.ok(CustomerResponse.from(service.getByRef(ref)));
    }

    @Operation(summary = "Create a customer",
            description = "Onboards a customer and runs simulated eKYC. A name containing 'FRAUD' is "
                    + "deterministically REJECTED, one containing 'REVIEW' goes to MANUAL_REVIEW, "
                    + "everything else is APPROVED.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CreateCustomerRequest request) {
        Customer customer = service.createCustomer(request.fullName(), request.email(), request.nationalId());
        return CustomerResponse.from(customer);
    }

    @GetMapping("/{customerRef}")
    public ResponseEntity<CustomerResponse> getByRef(@PathVariable String customerRef) {
        return ResponseEntity.ok(CustomerResponse.from(service.getByRef(customerRef)));
    }
}
