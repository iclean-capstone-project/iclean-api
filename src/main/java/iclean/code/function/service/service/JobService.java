package iclean.code.function.service.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.service.CreateServiceRequest;
import iclean.code.data.dto.request.service.UpdateServiceRequest;
import org.springframework.http.ResponseEntity;

public interface JobService {

    ResponseEntity<ResponseObject> getJobs();

    ResponseEntity<ResponseObject> createJob(CreateServiceRequest service);

    ResponseEntity<ResponseObject> updateJob(int jobId, UpdateServiceRequest newService);

    ResponseEntity<ResponseObject> deleteJob(int jobId);

    ResponseEntity<ResponseObject> getJobActives();
}
