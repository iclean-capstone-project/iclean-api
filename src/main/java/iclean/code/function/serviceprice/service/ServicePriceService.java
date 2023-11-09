package iclean.code.function.serviceprice.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.serviceprice.GetServicePriceRequest;
import org.springframework.http.ResponseEntity;

public interface ServicePriceService {
    ResponseEntity<ResponseObject> getServicePriceActive(GetServicePriceRequest request);

    Double getServicePrice(GetServicePriceRequest request);
    Double getServiceHelperPrice(GetServicePriceRequest request);
}
