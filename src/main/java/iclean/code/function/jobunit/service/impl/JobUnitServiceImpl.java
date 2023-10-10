package iclean.code.function.jobunit.service.impl;

import iclean.code.data.domain.JobUnit;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.jobunit.CreateJobUnitRequest;
import iclean.code.data.dto.request.jobunit.UpdateJobUnitRequest;
import iclean.code.data.enumjava.DeleteStatusEnum;
import iclean.code.data.repository.JobUnitRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.jobunit.service.JobUnitService;
import iclean.code.service.StorageService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class JobUnitServiceImpl implements JobUnitService {

    @Autowired
    private JobUnitRepository jobUnitRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getJobUnitActives() {
        try {
            List<JobUnit> jobUnits = jobUnitRepository.findAllActive();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Job", jobUnits));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getJobUnits() {
        try {
            List<JobUnit> jobs = jobUnitRepository.findAll();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                            "All Job", jobs));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createJobUnits(CreateJobUnitRequest request) {
        try {
            JobUnit job = modelMapper.map(request, JobUnit.class);
            String jobImgLink = storageService.uploadFile(request.getImgUnitFile());
            job.setImgJobUnit(jobImgLink);
            job.setCreateAt(Utils.getDateTimeNow());

            jobUnitRepository.save(job);
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
    public ResponseEntity<ResponseObject> updateJobUnit(int jobUnitId, UpdateJobUnitRequest request) {
        try {
            JobUnit jobUnit = findJobUnitById(jobUnitId);

            storageService.deleteFile(jobUnit.getImgJobUnit());
            String jobImgLink = storageService.uploadFile(request.getImgUnitFile());
            jobUnit.setImgJobUnit(jobImgLink);
            jobUnit.setUpdateAt(Utils.getDateTimeNow());
            modelMapper.map(request, jobUnit);

            jobUnitRepository.save(jobUnit);
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteJobUnit(int jobUnitId) {
        try {
            JobUnit jobUnit = findJobUnitById(jobUnitId);

            jobUnit.setIsDelete(DeleteStatusEnum.INACTIVE.getValue());
            jobUnitRepository.save(jobUnit);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete Job Successfully!", null));

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

    private JobUnit findJobUnitById(int id) {
        return jobUnitRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Job Unit ID %s are not exist", "id")));
    }
}
