package iclean.code.function.payment.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.function.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/v1/payments")
@Tag(name = "Payment Service")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-payment")
    @Operation(summary = "Create Payment", description = "Return success or fail message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create Payment success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createPayment(@RequestParam Long amount) {
        return paymentService.createPayment(amount);
    }

    @GetMapping("/return-payment")
    @Operation(summary = "Return Payment", description = "Return success or fail message after creating a payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return Payment success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> paymentReturn(HttpServletRequest request) {
        return paymentService.paymentReturn(request);
    }
}
