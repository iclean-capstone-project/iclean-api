package iclean.code.function.serviceprice.service;

import iclean.code.data.dto.common.ResponseObject;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;

public interface ServicePriceService {
    ResponseEntity<ResponseObject> getServicePriceActive(Integer jobUnitId, LocalTime startTime, Double hour);
}
