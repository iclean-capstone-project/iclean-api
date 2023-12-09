package iclean.code.function.serviceprice.service.impl;

import iclean.code.data.domain.ServiceUnit;
import iclean.code.data.domain.ServicePrice;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.serviceprice.GetServicePriceRequest;
import iclean.code.data.dto.request.serviceprice.GetServicePriceRequestDto;
import iclean.code.data.dto.request.serviceprice.ServicePriceRequest;
import iclean.code.data.dto.response.serviceprice.GetServicePriceResponse;
import iclean.code.data.dto.response.serviceprice.ServicePriceResponse;
import iclean.code.data.enumjava.ServicePriceEnum;
import iclean.code.data.repository.ServiceUnitRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ServicePriceServiceImpl implements ServicePriceService {

    @Autowired
    private ServicePriceRepository servicePriceRepository;

    @Autowired
    private ServiceUnitRepository serviceUnitRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getServicePriceActive(GetServicePriceRequest request) {
        try {
            Double totalPrice = getServicePrice(request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Not implement yet!", totalPrice));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public Double getServicePrice(GetServicePriceRequest request) {
        try {
            GetServicePriceRequestDto requestDto = modelMapper.map(request, GetServicePriceRequestDto.class);
            ServiceUnit serviceUnit = findServiceUnitById(requestDto.getServiceUnitId());
            LocalTime startTime = requestDto.getStartTime();
            Double hour = serviceUnit.getUnit().getUnitValue();
            LocalTime endTime = Utils.plusLocalTime(startTime, hour);
            double totalPrice = 0;
            List<ServicePrice> servicePrices = servicePriceRepository.findByServiceUnitId(serviceUnit.getServiceUnitId());
            if (!servicePrices.isEmpty()) {
                int i = 0;
                do {
                    if (i >= servicePrices.size()) {
                        break;
                    }
                    ServicePrice servicePrice = servicePrices.get(i);
                    if (Utils.isBeforeOrEqual(servicePrice.getEndTime(), startTime)) {
                        i++;
                        continue;
                    } else if (Utils.isBeforeOrEqual(servicePrice.getStartTime(), startTime)
                            && Utils.isBeforeOrEqual(servicePrice.getEndTime(), endTime)) {
                        Double numberHour = Utils.minusLocalTime(startTime, servicePrice.getEndTime());
                        totalPrice += servicePrice.getPrice() * numberHour;
                        startTime = Utils.plusLocalTime(startTime, numberHour);
                        i++;
                        continue;
                    } else if (Utils.isBeforeOrEqual(servicePrice.getStartTime(), startTime)
                            && Utils.isAfterOrEqual(servicePrice.getEndTime(), endTime)) {
                        Double numberHour = Utils.minusLocalTime(startTime, endTime);
                        totalPrice += servicePrice.getPrice() * numberHour;
                        startTime = Utils.plusLocalTime(startTime, numberHour);
                        continue;
                    }
                    Double numberHour = Utils.minusLocalTime(startTime, servicePrice.getStartTime());
                    totalPrice += serviceUnit.getDefaultPrice() * numberHour;
                    startTime = Utils.plusLocalTime(startTime, numberHour);

                } while (startTime.isBefore(endTime) && startTime.plusSeconds(20).isBefore(endTime));
            } else {
                totalPrice = serviceUnit.getDefaultPrice() * serviceUnit.getUnit().getUnitValue();
            }
            return totalPrice;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public Double getServiceHelperPrice(GetServicePriceRequest request) {
        try {
            GetServicePriceRequestDto requestDto = modelMapper.map(request, GetServicePriceRequestDto.class);
            ServiceUnit serviceUnit = findServiceUnitById(requestDto.getServiceUnitId());
            LocalTime startTime = requestDto.getStartTime();
            Double hour = serviceUnit.getUnit().getUnitValue();
            LocalTime endTime = Utils.plusLocalTime(startTime, hour);
            double totalPriceHelper = 0;
            List<ServicePrice> servicePrices = servicePriceRepository.findByServiceUnitId(serviceUnit.getServiceUnitId());
            if (!servicePrices.isEmpty()) {
                int i = 0;
                do {
                    if (i >= servicePrices.size()) {
                        break;
                    }
                    ServicePrice servicePrice = servicePrices.get(i);
                    if (Utils.isBeforeOrEqual(servicePrice.getEndTime(), startTime)) {
                        i++;
                        continue;
                    } else if (Utils.isBeforeOrEqual(servicePrice.getStartTime(), startTime)
                            && Utils.isBeforeOrEqual(servicePrice.getEndTime(), endTime)) {
                        Double numberHour = Utils.minusLocalTime(startTime, servicePrice.getEndTime());
                        totalPriceHelper += servicePrice.getPrice() * numberHour * servicePrice.getEmployeeCommission() / 100;
                        startTime = Utils.plusLocalTime(startTime, numberHour);
                        i++;
                        continue;
                    } else if (Utils.isBeforeOrEqual(servicePrice.getStartTime(), startTime)
                            && Utils.isAfterOrEqual(servicePrice.getEndTime(), endTime)) {
                        Double numberHour = Utils.minusLocalTime(startTime, endTime);
                        totalPriceHelper += servicePrice.getPrice() * numberHour * servicePrice.getEmployeeCommission() / 100;
                        startTime = Utils.plusLocalTime(startTime, numberHour);
                        continue;
                    }
                    Double numberHour = Utils.minusLocalTime(startTime, servicePrice.getStartTime());
                    totalPriceHelper += serviceUnit.getDefaultPrice() * numberHour * serviceUnit.getHelperCommission() / 100;
                    startTime = Utils.plusLocalTime(startTime, numberHour);

                } while (startTime.isBefore(endTime) && startTime.plusSeconds(20).isBefore(endTime));
            } else {
                totalPriceHelper = serviceUnit.getDefaultPrice() * serviceUnit.getUnit().getUnitValue() * serviceUnit.getHelperCommission() / 100;;
            }
            return totalPriceHelper;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createServicePrice(List<ServicePriceRequest> requests, Integer serviceUnitId) {
        try {
            ServiceUnit serviceUnit = findServiceUnitById(serviceUnitId);
            List<ServicePrice> servicePrices = serviceUnit.getServicePrices();
            for (ServicePrice servicePrice :
                    servicePrices) {
                servicePrice.setIsDelete(true);
            }
            List<ServicePrice> createServicePrices = new ArrayList<>();
            for (ServicePriceRequest request :
                    requests) {
                ServicePriceEnum servicePriceEnum = ServicePriceEnum.getById(request.getId());
                ServicePrice servicePrice = new ServicePrice();
                servicePrice.setPrice(request.getPrice());
                servicePrice.setServicePriceEnum(servicePriceEnum);
                servicePrice.setEmployeeCommission(request.getEmployeeCommission());
                servicePrice.setStartTime(Utils.convertToLocalTime(servicePriceEnum.getStartDate()));
                servicePrice.setEndTime(Utils.convertToLocalTime(servicePriceEnum.getEndDate()));
                servicePrice.setServiceUnit(serviceUnit);
                createServicePrices.add(servicePrice);
            }
            servicePriceRepository.saveAll(servicePrices);
            servicePriceRepository.saveAll(createServicePrices);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update service price successful!", null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getServicePrice(Integer serviceUnitId) {
        try {
            ServiceUnit serviceUnit = findServiceUnitById(serviceUnitId);
            List<ServicePrice> servicePrices = serviceUnit.getServicePrices();
            List<GetServicePriceResponse> responses = servicePrices
                    .stream()
                    .map((element -> {
                        GetServicePriceResponse response = modelMapper.map(element, GetServicePriceResponse.class);
                        response.setId(element.getServicePriceEnum().getId());
                        return response;
                    }))
                    .collect(Collectors.toList());
            List<ServicePriceEnum> enumsNotInResponses = Arrays.stream(ServicePriceEnum.values())
                    .filter(enumValue -> servicePrices.stream().noneMatch(servicePrice -> servicePrice.getServicePriceEnum() == enumValue))
                    .collect(Collectors.toList());
            for (ServicePriceEnum servicePriceEnum :
                    enumsNotInResponses) {
                GetServicePriceResponse getServicePriceResponse = new GetServicePriceResponse();
                getServicePriceResponse.setId(servicePriceEnum.getId());
                getServicePriceResponse.setPrice(serviceUnit.getDefaultPrice());
                getServicePriceResponse.setEmployeeCommission(serviceUnit.getHelperCommission());
                getServicePriceResponse.setStartTime(servicePriceEnum.getStartDate());
                getServicePriceResponse.setEndTime(servicePriceEnum.getEndDate());
                responses.add(getServicePriceResponse);
            }
            List<GetServicePriceResponse> sortedResponses = responses.stream()
                    .sorted(Comparator.comparing(GetServicePriceResponse::getId))
                    .collect(Collectors.toList());
            ServicePriceResponse servicePriceResponse = new ServicePriceResponse();
            servicePriceResponse.setServiceUnitId(serviceUnit.getServiceUnitId());
            servicePriceResponse.setResponses(sortedResponses);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update service price successful!", servicePriceResponse));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    private ServiceUnit findServiceUnitById(int id) {
        return serviceUnitRepository.findById(id).orElseThrow(()-> new NotFoundException(String.format("Service Unit ID %s is not found", id)));
    }
}
