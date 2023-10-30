package iclean.code.function.registeremployee.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.employeeinformation.CreateEmployeeInformationRequestDTO;
import iclean.code.data.dto.request.employeeinformation.GetEmployeeInformationRequestDTO;
import iclean.code.data.dto.request.employeeinformation.UpdateEmployeeInformationRequestDTO;
import org.springframework.http.ResponseEntity;

public interface RegisterEmployeeService {
    ResponseEntity<ResponseObject> getRegisterEmployees(GetEmployeeInformationRequestDTO request);

    ResponseEntity<ResponseObject> getRegisterEmployee(Integer id);

    ResponseEntity<ResponseObject> createRegisterEmployee(CreateEmployeeInformationRequestDTO request);

    ResponseEntity<ResponseObject> updateRegisterEmployee(Integer id, UpdateEmployeeInformationRequestDTO request);

    ResponseEntity<ResponseObject> deleteRegisterEmployee(Integer id);
}
