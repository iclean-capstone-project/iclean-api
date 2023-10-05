package iclean.code.function.wallethistory.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.wallethistory.CreateWalletHistoryRequestDTO;
import iclean.code.data.dto.request.wallethistory.UpdateWalletHistoryRequestDTO;
import org.springframework.http.ResponseEntity;

public interface WalletHistoryService {
    ResponseEntity<ResponseObject> getWalletHistories(Integer userId);

    ResponseEntity<ResponseObject> getWalletHistory(Integer id, Integer userId);

    ResponseEntity<ResponseObject> createWalletHistory(CreateWalletHistoryRequestDTO request, Integer userId);

    ResponseEntity<ResponseObject> updateWalletHistory(Integer id, UpdateWalletHistoryRequestDTO request, Integer userId);

    ResponseEntity<ResponseObject> deleteWalletHistory(Integer id, Integer userId);
}
