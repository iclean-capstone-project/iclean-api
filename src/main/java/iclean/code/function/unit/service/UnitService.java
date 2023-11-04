package iclean.code.function.unit.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.unit.CreateUnitRequest;
import iclean.code.data.dto.request.unit.UpdateUnitRequest;
import org.springframework.http.ResponseEntity;

public interface UnitService {
    ResponseEntity<ResponseObject> getUnits();

    ResponseEntity<ResponseObject> createUnit(CreateUnitRequest request);

    ResponseEntity<ResponseObject> updateUnit(int id, UpdateUnitRequest request);

    ResponseEntity<ResponseObject> deleteUnit(int jobUnitId);
}
