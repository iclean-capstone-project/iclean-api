package iclean.code.function.serviceregistration.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.serviceregistration.UpdateStatusServiceRegistrationRequest;
import org.springframework.http.ResponseEntity;

public interface ServiceRegistrationService {
    ResponseEntity<ResponseObject> updateServiceByHelper(Integer userId, Integer id,
                                                         UpdateStatusServiceRegistrationRequest request);

    ResponseEntity<ResponseObject> getServiceRegistrationActive(Integer userId);
}
