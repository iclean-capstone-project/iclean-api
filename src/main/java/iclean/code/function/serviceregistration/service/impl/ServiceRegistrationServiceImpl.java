package iclean.code.function.serviceregistration.service.impl;

import iclean.code.data.domain.ServiceRegistration;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.serviceregistration.UpdateStatusServiceRegistrationRequest;
import iclean.code.data.dto.response.serviceregistration.GetServiceOfHelperResponse;
import iclean.code.data.enumjava.ServiceHelperStatusEnum;
import iclean.code.data.repository.*;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.serviceregistration.service.ServiceRegistrationService;
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
public class ServiceRegistrationServiceImpl implements ServiceRegistrationService {
    @Autowired
    private ServiceRegistrationRepository serviceRegistrationRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> updateServiceByHelper(Integer userId, Integer registrationServiceId,
                                                                UpdateStatusServiceRegistrationRequest request) {
        try {
            ServiceRegistration serviceRegistration = findServiceRegistrationById(registrationServiceId);
            if (!Objects.equals(serviceRegistration.getHelperInformation().getUser().getUserId(), userId))
                throw new UserNotHavePermissionException("User not have permission to do this action!");
            serviceRegistration.setServiceHelperStatus(ServiceHelperStatusEnum.valueOf(request.getStatus().toUpperCase()));
            serviceRegistrationRepository.save(serviceRegistration);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Cancel a service by helper successful!",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.toString(),
                                null));
            }
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getServiceRegistrationActive(Integer userId) {
        try {
            List<ServiceRegistration> serviceRegistrations = serviceRegistrationRepository.findServiceRegistrationByUserId(userId);
            List<ServiceRegistration> filteredList = serviceRegistrations.stream()
                    .filter(registration -> registration.getServiceHelperStatus().equals(ServiceHelperStatusEnum.ACTIVE) ||
                            registration.getServiceHelperStatus().equals(ServiceHelperStatusEnum.INACTIVE))
                    .collect(Collectors.toList());
            List<GetServiceOfHelperResponse> responses = filteredList
                    .stream()
                    .map(value -> {
                        GetServiceOfHelperResponse getServiceOfHelperResponse = modelMapper.map(value, GetServiceOfHelperResponse.class);
                        getServiceOfHelperResponse.setStatus(value.getServiceHelperStatus());
                        return getServiceOfHelperResponse;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Get services of a helper successful!",
                            responses));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.toString(),
                                null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!",
                            null));
        }
    }

    private ServiceRegistration findServiceRegistrationById(Integer registrationServiceId) {
        return serviceRegistrationRepository.findById(registrationServiceId).orElseThrow(() ->
                new NotFoundException("Service registration are not found!"));
    }
}
