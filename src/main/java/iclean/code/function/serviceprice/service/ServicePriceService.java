package iclean.code.function.serviceprice.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.serviceprice.GetServicePriceRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;

public interface ServicePriceService {
    ResponseEntity<ResponseObject> getServicePriceActive(GetServicePriceRequest request);
}
