package sg.edu.rp.sandbox.payment.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sg.edu.rp.sandbox.common.error.BusinessException;
import sg.edu.rp.sandbox.payment.service.PaymentService;
import sg.edu.rp.sandbox.payment.service.TransferResult;
import sg.edu.rp.sandbox.security.CurrentUser;

/**
 * Payment endpoints. The paying customer is taken from the authenticated token.
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;
    private final CurrentUser currentUser;

    public PaymentController(PaymentService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    /** Executes an account-to-account transfer owned by the authenticated customer. */
    @Operation(summary = "Transfer between accounts",
            description = "Moves money between accounts. The source must belong to the caller. Simulated AML "
                    + "holds transfers of 5000+ for review (202) and blocks 10000+ (422). Returns 409 on "
                    + "insufficient funds.")
    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResult transfer(@Valid @RequestBody TransferRequest request) {
        String ref = currentUser.customerReference()
                .orElseThrow(() -> new BusinessException("NO_CUSTOMER_REF",
                        "Authenticated token has no customer reference"));
        return service.transfer(ref, request.fromAccount(), request.toAccount(),
                request.amount(), request.description());
    }
}
