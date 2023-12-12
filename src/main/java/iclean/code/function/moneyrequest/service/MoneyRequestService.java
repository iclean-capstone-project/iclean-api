package iclean.code.function.moneyrequest.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.moneyrequest.CreateMoneyRequestRequest;
import iclean.code.data.dto.request.moneyrequest.ValidateMoneyRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface MoneyRequestService {
    ResponseEntity<ResponseObject> getMoneyRequests(String phoneNumber, Pageable pageable);

    ResponseEntity<ResponseObject> createMoneyRequest(CreateMoneyRequestRequest request);

    ResponseEntity<ResponseObject> validateMoneyRequest(ValidateMoneyRequest request);

    ResponseEntity<ResponseObject> resendOtp(String phoneNumber);

    ResponseEntity<ResponseObject> sendMoneyToHelper(int bookingDetailId);
}
