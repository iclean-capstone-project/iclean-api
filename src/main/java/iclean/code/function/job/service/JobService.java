package iclean.code.function.job.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.job.AddJobRequest;
import iclean.code.data.dto.request.job.UpdateJobRequest;
import org.springframework.http.ResponseEntity;

public interface JobService {

    ResponseEntity<ResponseObject> getAllJob();

    ResponseEntity<ResponseObject> addJob(AddJobRequest service);

    ResponseEntity<ResponseObject> updateJob(int jobId, UpdateJobRequest newService);

    ResponseEntity<ResponseObject> deleteJob(int jobId);
}
