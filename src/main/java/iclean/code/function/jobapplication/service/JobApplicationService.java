package iclean.code.function.jobapplication.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.jobapplication.CreateJobApplicationRequestDTO;
import iclean.code.data.dto.request.jobapplication.UpdateJobApplicationRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobApplicationService {
    ResponseEntity<ResponseObject> getJobApplications();

    ResponseEntity<ResponseObject> getJobApplication(Integer id);

    ResponseEntity<ResponseObject> createJobApplication(CreateJobApplicationRequestDTO request,
                                                        MultipartFile frontIdCard,
                                                        MultipartFile backIdCard,
                                                        MultipartFile avatar,
                                                        List<MultipartFile> others);

    ResponseEntity<ResponseObject> updateJobApplication(Integer id, UpdateJobApplicationRequestDTO request, MultipartFile file);

    ResponseEntity<ResponseObject> deleteJobApplication(Integer id);
}
