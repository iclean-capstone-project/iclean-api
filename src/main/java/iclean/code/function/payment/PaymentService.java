package iclean.code.function.payment;

import iclean.code.data.dto.common.ResponseObject;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface PaymentService {
    ResponseEntity<ResponseObject> createPayment(Long amount);

    ResponseEntity<ResponseObject> paymentReturn(HttpServletRequest request);
}
