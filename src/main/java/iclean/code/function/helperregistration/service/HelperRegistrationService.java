package iclean.code.function.helperregistration.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.attachment.HelperRegistrationRequest;
import iclean.code.data.dto.request.employeeinformation.CancelHelperRequest;
import iclean.code.data.dto.request.employeeinformation.GetEmployeeInformationRequestDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HelperRegistrationService {
    ResponseEntity<ResponseObject> getAllRequestToBecomeHelper(Integer managerId, Boolean isAllRequest, Pageable pageable);
    ResponseEntity<ResponseObject> getHelpersInformation(Integer managerId, Boolean isAllRequest, Pageable pageable);
    ResponseEntity<ResponseObject> updateMoreServiceForHelper(Integer userId, List<MultipartFile> applications, List<Integer> serviceId);
    ResponseEntity<ResponseObject> cancelHelperInformationRequest(Integer id, CancelHelperRequest request);
    ResponseEntity<ResponseObject> createHelperRegistration(HelperRegistrationRequest helperRegistrationRequest, Integer renterId);

    ResponseEntity<ResponseObject> getHelperInformation(Integer id);
}
