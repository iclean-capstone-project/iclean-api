package iclean.code.function.registeremployee.service.impl;

import iclean.code.data.domain.HelperInformation;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.employeeinformation.CreateEmployeeInformationRequestDTO;
import iclean.code.data.dto.request.employeeinformation.GetEmployeeInformationRequestDTO;
import iclean.code.data.dto.request.employeeinformation.UpdateEmployeeInformationRequestDTO;
import iclean.code.data.dto.response.employeeinformation.GetEmployeeInformationResponseDTO;
import iclean.code.data.repository.RegisterEmployeeRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.registeremployee.service.RegisterEmployeeService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RegisterEmployeeServiceImpl implements RegisterEmployeeService {
    @Autowired
    private RegisterEmployeeRepository registerEmployeeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getRegisterEmployees(GetEmployeeInformationRequestDTO request) {
        try {
            List<HelperInformation> helperInformations = registerEmployeeRepository.findAll();
            List<GetEmployeeInformationResponseDTO> responses = helperInformations
                    .stream()
                    .map(employeeInformation -> modelMapper.map(employeeInformation, GetEmployeeInformationResponseDTO.class))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Register Employee List",
                            responses));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getRegisterEmployee(Integer id) {
        try {
            HelperInformation helperInformation = findRegisterEmployeeById(id);
            GetEmployeeInformationResponseDTO responses = modelMapper.map(helperInformation, GetEmployeeInformationResponseDTO.class);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Register Employee Information",
                            responses));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.toString(),
                                null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createRegisterEmployee(CreateEmployeeInformationRequestDTO request) {
        try {
            HelperInformation helperInformation = modelMapper.map(request, HelperInformation.class);
            User user = findUserById(request.getUserId());
            helperInformation.setUser(user);
            registerEmployeeRepository.save(helperInformation);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create new Register Employee Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateRegisterEmployee(Integer id, UpdateEmployeeInformationRequestDTO request) {
        try {
            HelperInformation helperInformation = findRegisterEmployeeById(id);
            if (!Objects.equals(helperInformation.getUser().getUserId(), request.getUserId()))
                throw new UserNotHavePermissionException();

            helperInformation = modelMapper.map(request, HelperInformation.class);
            registerEmployeeRepository.save(helperInformation);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Register Employee Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteRegisterEmployee(Integer id) {
        try {
            HelperInformation helperInformation = findRegisterEmployeeById(id);
            Integer fakeId = 1;
            if (!Objects.equals(helperInformation.getUser().getUserId(), fakeId))
                throw new UserNotHavePermissionException();

            registerEmployeeRepository.delete(helperInformation);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete Register Employee Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }
    private User findUserById(Integer id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User ID: %s is not exist", id)));
    }

    private HelperInformation findRegisterEmployeeById(Integer id) {
        return registerEmployeeRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Register Employee ID: %s is not exist", id)));
    }
}
