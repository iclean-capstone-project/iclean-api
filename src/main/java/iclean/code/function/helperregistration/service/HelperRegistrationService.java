package iclean.code.function.helperregistration.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.helperinformation.AcceptHelperRequest;
import iclean.code.data.dto.request.helperinformation.ConfirmHelperRequest;
import iclean.code.data.dto.request.helperinformation.HelperRegistrationRequest;
import iclean.code.data.dto.request.helperinformation.CancelHelperRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HelperRegistrationService {
    ResponseEntity<ResponseObject> getAllRequestToBecomeHelper(Integer managerId, Boolean isAllRequest, String startDate, String endDate, List<String> statuses, Pageable pageable);
    ResponseEntity<ResponseObject> getHelpersInformation(Integer managerId, Boolean isAllRequest, Pageable pageable);
    ResponseEntity<ResponseObject> updateMoreServiceForHelper(Integer userId, List<MultipartFile> applications, List<Integer> serviceId);
    ResponseEntity<ResponseObject> cancelHelperInformationRequest(Integer managerId, Integer id, CancelHelperRequest request);
    ResponseEntity<ResponseObject> createHelperRegistration(HelperRegistrationRequest helperRegistrationRequest, Integer renterId);
    ResponseEntity<ResponseObject> getHelperInformation(Integer id);
    ResponseEntity<ResponseObject> acceptHelperInformation(Integer userId, Integer id);
    ResponseEntity<ResponseObject> confirmHelperInformation(Integer managerId, Integer id, ConfirmHelperRequest request);
    ResponseEntity<ResponseObject> assignManageToRegistration();

}
