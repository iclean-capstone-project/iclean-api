package iclean.code.function.attachment.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.attachment.CreateAttachmentRequestDTO;
import iclean.code.data.dto.request.attachment.UpdateAttachmentRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobApplicationService {
    ResponseEntity<ResponseObject> getJobApplications();

    ResponseEntity<ResponseObject> getJobApplication(Integer id);

    ResponseEntity<ResponseObject> createJobApplication(CreateAttachmentRequestDTO request,
                                                        MultipartFile frontIdCard,
                                                        MultipartFile backIdCard,
                                                        MultipartFile avatar,
                                                        List<MultipartFile> others);

    ResponseEntity<ResponseObject> updateJobApplication(Integer id, UpdateAttachmentRequestDTO request, MultipartFile file);

    ResponseEntity<ResponseObject> deleteJobApplication(Integer id);
}
