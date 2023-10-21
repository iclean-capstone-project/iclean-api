package iclean.code.function.transaction.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.transaction.TransactionRequestDto;
import org.springframework.http.ResponseEntity;

public interface TransactionService {
    ResponseEntity<ResponseObject> getTransactions(Integer userId);

    ResponseEntity<ResponseObject> getTransaction(Integer id, Integer userId);

    ResponseEntity<ResponseObject> createTransaction(TransactionRequestDto request);
}
