package iclean.code.function.serviceunit.service.impl;

import iclean.code.data.domain.Service;
import iclean.code.data.domain.ServicePrice;
import iclean.code.data.domain.ServiceUnit;
import iclean.code.data.domain.Unit;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.serviceprice.ServicePriceRequest;
import iclean.code.data.dto.request.serviceunit.CreateServiceUnitRequest;
import iclean.code.data.dto.request.serviceunit.UpdateServiceUnitRequest;
import iclean.code.data.dto.response.serviceprice.GetServicePriceResponse;
import iclean.code.data.dto.response.serviceunit.GetServiceUnitDetailResponse;
import iclean.code.data.dto.response.serviceunit.GetServiceUnitResponse;
import iclean.code.data.dto.response.serviceunit.GetServiceUnitResponseForRenter;
import iclean.code.data.enumjava.DeleteStatusEnum;
import iclean.code.data.enumjava.ServicePriceEnum;
import iclean.code.data.repository.ServicePriceRepository;
import iclean.code.data.repository.ServiceRepository;
import iclean.code.data.repository.ServiceUnitRepository;
import iclean.code.data.repository.UnitRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.serviceunit.service.ServiceUnitService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Log4j2
public class ServiceUnitServiceImpl implements ServiceUnitService {
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ServiceUnitRepository serviceUnitRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private ServicePriceRepository servicePriceRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getServiceUnitsForRenter(Integer serviceId) {
        try {
            findServiceById(serviceId);
            List<ServiceUnit> serviceUnits = serviceUnitRepository.findByServiceActive(serviceId);
            List<GetServiceUnitResponseForRenter> dtoList = serviceUnits
                    .stream()
                    .map(serviceUnit ->  {
                        GetServiceUnitResponseForRenter response = modelMapper.map(serviceUnit, GetServiceUnitResponseForRenter.class);
                        response.setValue(serviceUnit.getUnit().getUnitDetail());
                        response.setEquivalent(serviceUnit.getUnit().getUnitValue());
                        return response;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Service Unit Active", dtoList));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createServiceUnit(CreateServiceUnitRequest request) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            ServiceUnit serviceUnit = new ServiceUnit();
            modelMapper.map(request, serviceUnit);
            Service service = findServiceById(request.getServiceId());
            Unit unit = findUnitById(request.getUnitId());
            serviceUnit.setService(service);
            serviceUnit.setUnit(unit);

            List<ServicePrice> createServicePrices = new ArrayList<>();
            for (ServicePriceRequest element :
                    request.getServicePrices()) {
                ServicePriceEnum servicePriceEnum = ServicePriceEnum.getById(element.getId());
                ServicePrice servicePrice = new ServicePrice();
                servicePrice.setPrice(element.getPrice());
                servicePrice.setServicePriceEnum(servicePriceEnum);
                servicePrice.setEmployeeCommission(element.getEmployeeCommission());
                servicePrice.setStartTime(Utils.convertToLocalTime(servicePriceEnum.getStartDate()));
                servicePrice.setEndTime(Utils.convertToLocalTime(servicePriceEnum.getEndDate()));
                servicePrice.setServiceUnit(serviceUnit);
                createServicePrices.add(servicePrice);
            }
            servicePriceRepository.saveAll(createServicePrices);
            serviceUnitRepository.save(serviceUnit);
            transactionManager.commit(status);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create Service Unit Successfully!", null));

        } catch (Exception e) {
            transactionManager.rollback(status);
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateServiceUnit(Integer id, UpdateServiceUnitRequest request) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            ServiceUnit serviceUnit = findById(id);
            modelMapper.map(request, serviceUnit);
            serviceUnit.setUpdateAt(Utils.getLocalDateTimeNow());
            List<ServicePrice> servicePrices = serviceUnit.getServicePrices();
            for (ServicePrice servicePrice :
                    servicePrices) {
                servicePrice.setIsDelete(true);
            }
            List<ServicePrice> createServicePrices = new ArrayList<>();
            for (ServicePriceRequest element :
                    request.getServicePriceRequests()) {
                ServicePriceEnum servicePriceEnum = ServicePriceEnum.getById(element.getId());
                ServicePrice servicePrice = new ServicePrice();
                servicePrice.setPrice(element.getPrice());
                servicePrice.setServicePriceEnum(servicePriceEnum);
                servicePrice.setEmployeeCommission(element.getEmployeeCommission());
                servicePrice.setStartTime(Utils.convertToLocalTime(servicePriceEnum.getStartDate()));
                servicePrice.setEndTime(Utils.convertToLocalTime(servicePriceEnum.getEndDate()));
                servicePrice.setServiceUnit(serviceUnit);
                createServicePrices.add(servicePrice);
            }
            servicePriceRepository.saveAll(servicePrices);
            servicePriceRepository.saveAll(createServicePrices);
            serviceUnitRepository.save(serviceUnit);
            transactionManager.commit(status);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Service Unit Successfully!", null));

        } catch (Exception e) {
            transactionManager.rollback(status);
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
    public ResponseEntity<ResponseObject> getServiceUnits(Integer serviceId, Sort sort) {
        try {
            findServiceById(serviceId);
            List<ServiceUnit> serviceUnits = serviceUnitRepository.findByService(serviceId, sort);
            List<GetServiceUnitResponse> dtoList = serviceUnits
                    .stream()
                    .map(serviceUnit ->  {
                        GetServiceUnitResponse response =  modelMapper.map(serviceUnit, GetServiceUnitResponse.class);
                        response.setUnitId(serviceUnit.getUnit().getUnitId());
                        response.setUnitDetail(serviceUnit.getUnit().getUnitDetail());
                        response.setUnitValue(serviceUnit.getUnit().getUnitValue());
                        return response;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Service Unit", dtoList));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getServiceUnit(Integer serviceUnitId) {
        try {
            ServiceUnit serviceUnit = findById(serviceUnitId);
            List<ServicePrice> servicePrices = servicePriceRepository.findByServiceUnitId(serviceUnitId);
            GetServiceUnitDetailResponse data = modelMapper.map(serviceUnit, GetServiceUnitDetailResponse.class);
            data.setUnitId(serviceUnit.getUnit().getUnitId());
            data.setUnitDetail(serviceUnit.getUnit().getUnitDetail());
            data.setUnitValue(serviceUnit.getUnit().getUnitValue());
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
            data.setServicePrices(sortedResponses);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Service Unit", data));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteServiceUnit(Integer id) {
        try {
            ServiceUnit serviceUnit = findById(id);
            serviceUnit.setIsDeleted(DeleteStatusEnum.INACTIVE.getValue());
            serviceUnitRepository.save(serviceUnit);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete Service Unit Successfully!", null));

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

    private ServiceUnit findById(Integer id) {
        return serviceUnitRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Service Unit ID %s is not found", id)));
    }

    private Service findServiceById(Integer id) {
        return serviceRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Service ID %s is not found", id)));
    }

    private Unit findUnitById(Integer id) {
        return unitRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Unit ID %s is not found", id)));
    }
}
