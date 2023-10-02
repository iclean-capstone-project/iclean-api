package iclean.code.function.wallethistory.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.wallethistory.CreateWalletHistoryRequestDTO;
import iclean.code.data.dto.request.wallethistory.GetWalletHistoryRequestDTO;
import iclean.code.data.dto.request.wallethistory.UpdateWalletHistoryRequestDTO;
import org.springframework.http.ResponseEntity;

public interface WalletHistoryService {
    ResponseEntity<ResponseObject> getWalletHistories(GetWalletHistoryRequestDTO request);

    ResponseEntity<ResponseObject> getWalletHistory(Integer id);

    ResponseEntity<ResponseObject> createWalletHistory(CreateWalletHistoryRequestDTO request);

    ResponseEntity<ResponseObject> updateWalletHistory(Integer id, UpdateWalletHistoryRequestDTO request);

    ResponseEntity<ResponseObject> deleteWalletHistory(Integer id);
}
