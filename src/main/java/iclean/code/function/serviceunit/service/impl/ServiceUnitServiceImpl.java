package iclean.code.function.serviceunit.service.impl;

import iclean.code.data.domain.Service;
import iclean.code.data.domain.ServiceUnit;
import iclean.code.data.domain.Unit;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.serviceunit.CreateServiceUnitRequest;
import iclean.code.data.dto.request.serviceunit.UpdateServiceUnitRequest;
import iclean.code.data.dto.response.serviceunit.GetServiceUnitResponse;
import iclean.code.data.dto.response.serviceunit.GetServiceUnitResponseForRenter;
import iclean.code.data.enumjava.DeleteStatusEnum;
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
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getServiceUnitsForRenter(Integer serviceId) {
        try {
            findServiceById(serviceId);
            List<ServiceUnit> serviceUnits = serviceUnitRepository.findByServiceActive(serviceId);
            List<GetServiceUnitResponseForRenter> dtoList = serviceUnits
                    .stream()
                    .map(serviceUnit -> modelMapper.map(serviceUnit, GetServiceUnitResponseForRenter.class))
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
        try {
            ServiceUnit serviceUnit = new ServiceUnit();
            modelMapper.map(request, serviceUnit);
            Service service = findServiceById(request.getServiceId());
            Unit unit = findUnitById(request.getUnitId());
            serviceUnit.setService(service);
            serviceUnit.setUnit(unit);

            serviceUnitRepository.save(serviceUnit);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create Service Unit Successfully!", null));

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
    public ResponseEntity<ResponseObject> updateServiceUnit(Integer id, UpdateServiceUnitRequest request) {
        try {
            ServiceUnit serviceUnit = findById(id);
            modelMapper.map(request, serviceUnit);
            if (!Utils.isNullOrEmpty(request.getServiceUnitStatus())) {
                serviceUnit.setIsDeleted(DeleteStatusEnum.valueOf(request.getServiceUnitStatus().toUpperCase()).getValue());
            }
            serviceUnit.setUpdateAt(Utils.getDateTimeNow());

            serviceUnitRepository.save(serviceUnit);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Service Unit Successfully!", null));

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
    public ResponseEntity<ResponseObject> getServiceUnits(Integer serviceId, Sort sort) {
        try {
            findServiceById(serviceId);
            List<ServiceUnit> serviceUnits = serviceUnitRepository.findByService(serviceId, sort);
            List<GetServiceUnitResponse> dtoList = serviceUnits
                    .stream()
                    .map(serviceUnit -> modelMapper.map(serviceUnit, GetServiceUnitResponse.class))
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
