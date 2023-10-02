package iclean.code.function.moneyrequest.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.moneyrequest.CreateMoneyRequestRequestDTO;
import iclean.code.data.dto.request.moneyrequest.GetMoneyRequestRequestDTO;
import iclean.code.data.dto.request.moneyrequest.UpdateMoneyRequestRequestDTO;
import org.springframework.http.ResponseEntity;

public interface MoneyRequestService {
    ResponseEntity<ResponseObject> getMoneyRequests(GetMoneyRequestRequestDTO request);

    ResponseEntity<ResponseObject> getMoneyRequest(Integer id);

    ResponseEntity<ResponseObject> createMoneyRequest(CreateMoneyRequestRequestDTO request);

    ResponseEntity<ResponseObject> updateMoneyRequest(Integer id, UpdateMoneyRequestRequestDTO request);

    ResponseEntity<ResponseObject> deleteMoneyRequest(Integer id);
}
