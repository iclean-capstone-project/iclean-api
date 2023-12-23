package iclean.code.function.serviceunit.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.serviceunit.CreateServiceUnitRequest;
import iclean.code.data.dto.request.serviceunit.UpdateServiceUnitRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

public interface ServiceUnitService {
    ResponseEntity<ResponseObject> getServiceUnitsForRenter(Integer serviceId);
    ResponseEntity<ResponseObject> createServiceUnit(CreateServiceUnitRequest request);
    ResponseEntity<ResponseObject> updateServiceUnit(Integer id, UpdateServiceUnitRequest request);
    ResponseEntity<ResponseObject> getServiceUnits(Integer serviceId, Sort sort);
    ResponseEntity<ResponseObject> getServiceUnit(Integer serviceUnitId);
    ResponseEntity<ResponseObject> deleteServiceUnit(Integer id);

}
