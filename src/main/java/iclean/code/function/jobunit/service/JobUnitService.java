package iclean.code.function.jobunit.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.serviceunit.CreateServiceUnitRequest;
import iclean.code.data.dto.request.serviceunit.UpdateServiceUnitRequest;
import org.springframework.http.ResponseEntity;

public interface JobUnitService {
    ResponseEntity<ResponseObject> getJobUnitActives();

    ResponseEntity<ResponseObject> getJobUnits();

    ResponseEntity<ResponseObject> createJobUnits(CreateServiceUnitRequest request);

    ResponseEntity<ResponseObject> updateJobUnit(int jobUnitId, UpdateServiceUnitRequest request);

    ResponseEntity<ResponseObject> deleteJobUnit(int jobUnitId);
}
