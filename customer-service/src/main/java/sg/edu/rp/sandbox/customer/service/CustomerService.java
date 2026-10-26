package sg.edu.rp.sandbox.customer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sg.edu.rp.sandbox.common.error.BusinessException;
import sg.edu.rp.sandbox.common.kyc.KycProvider;
import sg.edu.rp.sandbox.common.kyc.KycRequest;
import sg.edu.rp.sandbox.common.kyc.KycResult;
import sg.edu.rp.sandbox.customer.api.CreateCustomerRequest;
import sg.edu.rp.sandbox.customer.domain.Customer;
import sg.edu.rp.sandbox.customer.domain.CustomerRepository;

import java.util.UUID;

/**
 * Customer onboarding. Runs a KYC check (via the pluggable {@link KycProvider})
 * on creation and stores the resulting status.
 */
@Service
public class CustomerService {

    private final CustomerRepository repository;
    private final KycProvider kycProvider;

    public CustomerService(CustomerRepository repository, KycProvider kycProvider) {
        this.repository = repository;
        this.kycProvider = kycProvider;
    }

    @Transactional
    public Customer createCustomer(CreateCustomerRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new BusinessException("EMAIL_IN_USE", "A customer with this email already exists");
        }

        // Pluggable KYC: simulated in v1, swappable for a vendor via config.
        KycResult kyc = kycProvider.verify(
                new KycRequest(request.fullName(), request.email(), request.nationalId()));

        Customer customer = new Customer(
                UUID.randomUUID().toString(), request.fullName(), request.email(), request.nationalId(),
                kyc.status(), kyc.reason());
        customer.setProfile(
                request.phone(), request.addressLine1(), request.addressLine2(), request.city(),
                request.postalCode(), request.country(), request.dateOfBirth(),
                request.occupation(), request.preferredChannel());
        return repository.save(customer);
    }

    @Transactional(readOnly = true)
    public Customer getByRef(String customerRef) {
        return repository.findByCustomerRef(customerRef)
                .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND", "No customer with ref " + customerRef));
    }
}
