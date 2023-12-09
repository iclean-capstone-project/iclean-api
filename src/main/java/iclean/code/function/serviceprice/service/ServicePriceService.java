package iclean.code.function.serviceprice.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.serviceprice.GetServicePriceRequest;
import iclean.code.data.dto.request.serviceprice.ServicePriceRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ServicePriceService {
    ResponseEntity<ResponseObject> getServicePriceActive(GetServicePriceRequest request);

    Double getServicePrice(GetServicePriceRequest request);
    Double getServiceHelperPrice(GetServicePriceRequest request);
    ResponseEntity<ResponseObject> createServicePrice(List<ServicePriceRequest> requests, Integer serviceUnitId);
    ResponseEntity<ResponseObject> getServicePrice(Integer serviceUnitId);
}
