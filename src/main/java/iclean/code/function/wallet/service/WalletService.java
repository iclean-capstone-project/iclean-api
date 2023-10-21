package iclean.code.function.wallet.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.wallet.UpdateBalance;
import org.springframework.http.ResponseEntity;

public interface WalletService {
    ResponseEntity<ResponseObject> getCurrentBalance(Integer userId, String walletType);

    ResponseEntity<ResponseObject> updateBalanceByUserId(int userId, UpdateBalance updateBalance);
}
