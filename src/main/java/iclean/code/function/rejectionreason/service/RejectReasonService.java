package iclean.code.function.rejectionreason.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.rejectionreason.CreateRejectionReasonRequestDTO;
import iclean.code.data.dto.request.rejectionreason.UpdateRejectionReasonRequestDTO;
import org.springframework.http.ResponseEntity;

public interface RejectReasonService {
    ResponseEntity<ResponseObject> getRejectionReasons();
    ResponseEntity<ResponseObject> createRejectReason(CreateRejectionReasonRequestDTO request);

    ResponseEntity<ResponseObject> updateRejectReason(Integer id, UpdateRejectionReasonRequestDTO request);

    ResponseEntity<ResponseObject> deleteRejectReason(Integer id);
}
