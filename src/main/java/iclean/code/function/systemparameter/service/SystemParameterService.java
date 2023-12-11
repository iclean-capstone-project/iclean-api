package iclean.code.function.systemparameter.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.systemparameter.UpdateSystemParameter;
import org.springframework.http.ResponseEntity;

public interface SystemParameterService {
    ResponseEntity<ResponseObject> getAllSystemParameter();
    ResponseEntity<ResponseObject> updateSystemParameter(UpdateSystemParameter systemParameter);
}
