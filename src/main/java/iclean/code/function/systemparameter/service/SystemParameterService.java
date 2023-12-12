package iclean.code.function.systemparameter.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.systemparameter.UpdateSystemParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

public interface SystemParameterService {
    ResponseEntity<ResponseObject> getAllSystemParameter();
    ResponseEntity<ResponseObject> updateSystemParameter(List<UpdateSystemParameter> systemParameter);
}
