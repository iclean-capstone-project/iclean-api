package iclean.code.function.jobunit.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.jobunit.CreateJobUnitRequest;
import iclean.code.data.dto.request.jobunit.UpdateJobUnitRequest;
import org.springframework.http.ResponseEntity;

public interface JobUnitService {
    ResponseEntity<ResponseObject> getJobUnitActives();

    ResponseEntity<ResponseObject> getJobUnits();

    ResponseEntity<ResponseObject> createJobUnits(CreateJobUnitRequest request);

    ResponseEntity<ResponseObject> updateJobUnit(int jobUnitId, UpdateJobUnitRequest request);

    ResponseEntity<ResponseObject> deleteJobUnit(int jobUnitId);
}
