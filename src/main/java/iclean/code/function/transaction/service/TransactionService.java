package iclean.code.function.transaction.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.transaction.TransactionRequest;
import iclean.code.exception.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface TransactionService {
    ResponseEntity<ResponseObject> getTransactions(Integer userId, String walletType, Pageable pageable);

    ResponseEntity<ResponseObject> getTransaction(Integer id, Integer userId);

    ResponseEntity<ResponseObject> createTransaction(TransactionRequest request);
    boolean createTransactionService(TransactionRequest request) throws BadRequestException;
}
