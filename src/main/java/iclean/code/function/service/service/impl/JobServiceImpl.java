package iclean.code.function.service.service.impl;

import iclean.code.data.domain.Service;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.service.CreateServiceRequest;
import iclean.code.data.dto.request.service.UpdateServiceRequest;
import iclean.code.data.enumjava.DeleteStatusEnum;
import iclean.code.data.repository.ServiceRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.service.service.JobService;
import iclean.code.service.StorageService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@Log4j2
public class JobServiceImpl implements JobService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getJobs() {
        try {
            List<Service> services = serviceRepository.findAll();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Job", services));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createJob(CreateServiceRequest request) {
        try {
            Service service = modelMapper.map(request, Service.class);
            String jobImgLink = storageService.uploadFile(request.getImgService());
            service.setServiceImage(jobImgLink);
            service.setCreateAt(Utils.getDateTimeNow());

            serviceRepository.save(service);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create Job Successfully!", null));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateJob(int jobId, UpdateServiceRequest request) {
        try {
            Optional<Service> optionalJob = serviceRepository.findById(jobId);
            if (optionalJob.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "Job is not exist", null));

            Service serviceToUpdate = optionalJob.get();
            storageService.deleteFile(serviceToUpdate.getServiceImage());
            String jobImgLink = storageService.uploadFile(request.getServiceImageFile());
            serviceToUpdate.setServiceImage(jobImgLink);
            serviceToUpdate.setUpdateAt(Utils.getDateTimeNow());

            modelMapper.map(request, serviceToUpdate);

            serviceRepository.save(serviceToUpdate);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Job Successfully!", null));

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
    public ResponseEntity<ResponseObject> deleteJob(int jobId) {
        try {
            Optional<Service> optionalJob = serviceRepository.findById(jobId);
            if (optionalJob.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "Job is not exist", null));

            Service serviceToDelete = optionalJob.get();
            serviceToDelete.setIsDeleted(DeleteStatusEnum.INACTIVE.getValue());
            serviceRepository.save(serviceToDelete);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Delete Job Successfully!", null));

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
    public ResponseEntity<ResponseObject> getJobActives() {
        try {
            List<Service> services = serviceRepository.findAllActive();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Job", services));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }
}
