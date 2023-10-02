package iclean.code.function.rejectreason.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.rejectreason.CreateRejectReasonRequestDTO;
import iclean.code.data.dto.request.rejectreason.GetRejectReasonRequestDTO;
import iclean.code.data.dto.request.rejectreason.UpdateRejectReasonRequestDTO;
import org.springframework.http.ResponseEntity;

public interface RejectReasonService {
    ResponseEntity<ResponseObject> getRejectReasons(GetRejectReasonRequestDTO request);

    ResponseEntity<ResponseObject> getRejectReason(Integer id);

    ResponseEntity<ResponseObject> createRejectReason(CreateRejectReasonRequestDTO request);

    ResponseEntity<ResponseObject> updateRejectReason(Integer id, UpdateRejectReasonRequestDTO request);

    ResponseEntity<ResponseObject> deleteRejectReason(Integer id);
}
