package iclean.code.function.serviceprice.service.impl;

import iclean.code.data.domain.Unit;
import iclean.code.data.domain.ServicePrice;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.repository.UnitRepository;
import iclean.code.data.repository.ServicePriceRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.serviceprice.service.ServicePriceService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@Log4j2
public class ServicePriceImplement implements ServicePriceService {

    @Autowired
    private ServicePriceRepository servicePriceRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getServicePriceActive(Integer jobUnitId, LocalTime startTime, Double hour) {
        try {
            findJobUnitById(jobUnitId);
            long seconds = (long) (hour * 3600);
            LocalTime endTime = startTime.plusSeconds(seconds);
            double totalPrice = 0;
            List<ServicePrice> servicePrices = servicePriceRepository.findByStartTime(startTime, endTime);
            for (ServicePrice servicePrice:
                    servicePrices) {
                Double numberHour = Utils.minusLocalTime(startTime, servicePrice.getEndTime());
                totalPrice += servicePrice.getPrice() * numberHour;
                startTime = startTime.plusHours((long) (numberHour * 3600));

            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Not implement yet!", totalPrice));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    private Unit findJobUnitById(int id) {
        return unitRepository.findById(id).orElseThrow(()-> new NotFoundException("Job Unit not found"));
    }
}
