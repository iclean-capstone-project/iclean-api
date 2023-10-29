package iclean.code.function.moneyrequest.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.moneyrequest.CreateMoneyRequestRequestDTO;
import iclean.code.data.dto.request.moneyrequest.ValidateMoneyRequestDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface MoneyRequestService {
    ResponseEntity<ResponseObject> getMoneyRequests(String phoneNumber, Pageable pageable);

    ResponseEntity<ResponseObject> getMoneyRequest(Integer id);

    ResponseEntity<ResponseObject> createMoneyRequest(CreateMoneyRequestRequestDTO request);

    ResponseEntity<ResponseObject> validateMoneyRequest(ValidateMoneyRequestDTO request);

    ResponseEntity<ResponseObject> resendOtp(Integer id);
}
