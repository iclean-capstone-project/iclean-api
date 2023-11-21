package iclean.code.function.payment;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface PaymentService {
    ResponseEntity<?> createPayment();

    ResponseEntity<?> paymentReturn(HttpServletRequest request);
}
