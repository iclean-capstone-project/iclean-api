package iclean.code.function.unit.service;

import iclean.code.data.dto.common.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface UnitService {
    ResponseEntity<ResponseObject> getUnits();
}
