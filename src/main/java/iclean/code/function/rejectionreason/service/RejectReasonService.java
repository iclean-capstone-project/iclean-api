package iclean.code.function.rejectionreason.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.rejectionreason.CreateRejectionReasonRequest;
import iclean.code.data.dto.request.rejectionreason.UpdateRejectionReasonRequest;
import org.springframework.http.ResponseEntity;

public interface RejectReasonService {
    ResponseEntity<ResponseObject> getRejectionReasons();
}
