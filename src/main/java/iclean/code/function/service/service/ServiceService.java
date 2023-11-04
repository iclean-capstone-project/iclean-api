package iclean.code.function.service.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.service.CreateServiceRequest;
import iclean.code.data.dto.request.service.UpdateServiceRequest;
import org.springframework.http.ResponseEntity;

public interface ServiceService {

    ResponseEntity<ResponseObject> getServices();

    ResponseEntity<ResponseObject> createService(CreateServiceRequest service);

    ResponseEntity<ResponseObject> updateService(int serviceId, UpdateServiceRequest newService);

    ResponseEntity<ResponseObject> deleteService(int serviceId);

    ResponseEntity<ResponseObject> getServiceActives();

    ResponseEntity<ResponseObject> getService(int id);

    ResponseEntity<ResponseObject> getServiceForHelper(Integer serviceId);
}
