package iclean.code.function.registeremployee.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.registeremployee.CreateRegisterEmployeeRequestDTO;
import iclean.code.data.dto.request.registeremployee.GetRegisterEmployeeRequestDTO;
import iclean.code.data.dto.request.registeremployee.UpdateRegisterEmployeeRequestDTO;
import org.springframework.http.ResponseEntity;

public interface RegisterEmployeeService {
    ResponseEntity<ResponseObject> getRegisterEmployees(GetRegisterEmployeeRequestDTO request);

    ResponseEntity<ResponseObject> getRegisterEmployee(Integer id);

    ResponseEntity<ResponseObject> createRegisterEmployee(CreateRegisterEmployeeRequestDTO request);

    ResponseEntity<ResponseObject> updateRegisterEmployee(Integer id, UpdateRegisterEmployeeRequestDTO request);

    ResponseEntity<ResponseObject> deleteRegisterEmployee(Integer id);
}
