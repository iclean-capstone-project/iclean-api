package iclean.code.function.job.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.job.CreateJobRequest;
import iclean.code.data.dto.request.job.UpdateJobRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface JobService {

    ResponseEntity<ResponseObject> getJobs();

    ResponseEntity<ResponseObject> createJob(CreateJobRequest service);

    ResponseEntity<ResponseObject> updateJob(int jobId, UpdateJobRequest newService);

    ResponseEntity<ResponseObject> deleteJob(int jobId);

    ResponseEntity<ResponseObject> getJobActives();
}
