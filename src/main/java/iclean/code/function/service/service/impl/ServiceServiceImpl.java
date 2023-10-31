package iclean.code.function.service.service.impl;

import iclean.code.data.domain.Service;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.service.CreateServiceRequest;
import iclean.code.data.dto.request.service.UpdateServiceRequest;
import iclean.code.data.dto.response.service.GetServiceActiveResponse;
import iclean.code.data.dto.response.service.GetServiceDetailResponse;
import iclean.code.data.dto.response.service.GetServiceResponse;
import iclean.code.data.enumjava.DeleteStatusEnum;
import iclean.code.data.repository.ServiceRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.service.service.ServiceService;
import iclean.code.service.StorageService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Log4j2
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getServices() {
        try {
            List<Service> services = serviceRepository.findAll();
            List<GetServiceResponse> dtoList = services
                    .stream()
                    .map(service -> modelMapper.map(service, GetServiceResponse.class))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Service", dtoList));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createService(CreateServiceRequest request) {
        try {
            Service service = modelMapper.map(request, Service.class);
            String jobImgLink = storageService.uploadFile(request.getImgService());
            service.setServiceImage(jobImgLink);
            service.setCreateAt(Utils.getDateTimeNow());

            serviceRepository.save(service);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create Service Successfully!", null));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString()
                            , "Something wrong occur!", null));
        }
    }

    private Service findById(int id) {
        return serviceRepository.findById(id).orElseThrow(() -> new NotFoundException("Service is not exist"));
    }

    @Override
    public ResponseEntity<ResponseObject> updateService(int serviceId, UpdateServiceRequest request) {
        try {
            Service serviceToUpdate = findById(serviceId);
            modelMapper.map(request, serviceToUpdate);
            serviceToUpdate.setIsDeleted(DeleteStatusEnum.valueOf(request.getServiceStatus().toUpperCase()).getValue());
            if (request.getImgService() != null) {
                storageService.deleteFile(serviceToUpdate.getServiceImage());
                String jobImgLink = storageService.uploadFile(request.getImgService());
                serviceToUpdate.setServiceImage(jobImgLink);
            }
            serviceToUpdate.setUpdateAt(Utils.getDateTimeNow());

            serviceRepository.save(serviceToUpdate);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Service Successfully!", null));

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
    public ResponseEntity<ResponseObject> deleteService(int serviceId) {
        try {
            Service optionalService = findById(serviceId);
            optionalService.setIsDeleted(DeleteStatusEnum.INACTIVE.getValue());
            serviceRepository.save(optionalService);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete Service Successfully!", null));

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
    public ResponseEntity<ResponseObject> getServiceActives() {
        try {
            List<Service> services = serviceRepository.findAllActive();
            List<GetServiceActiveResponse> dtoList = services
                    .stream()
                    .map(service -> modelMapper.map(service, GetServiceActiveResponse.class))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Service", dtoList));
        } catch (Exception e) {
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
    public ResponseEntity<ResponseObject> getService(int id) {
        try {
            Service service = findById(id);
            GetServiceDetailResponse response = modelMapper.map(service, GetServiceDetailResponse.class);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Service Detail", response));
        } catch (Exception e) {
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
}
