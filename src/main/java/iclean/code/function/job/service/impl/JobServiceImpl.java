package iclean.code.function.job.service.impl;

import iclean.code.data.domain.Job;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.job.CreateJobRequest;
import iclean.code.data.dto.request.job.UpdateJobRequest;
import iclean.code.data.enumjava.DeleteStatusEnum;
import iclean.code.data.repository.JobRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.job.service.JobService;
import iclean.code.service.StorageService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getJobs() {
        try {
            List<Job> jobs = jobRepository.findAll();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Job", jobs));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createJob(CreateJobRequest request) {
        try {
            Job job = modelMapper.map(request, Job.class);
            String jobImgLink = storageService.uploadFile(request.getImgJob());
            job.setJobImage(jobImgLink);
            job.setCreateAt(Utils.getDateTimeNow());

            jobRepository.save(job);
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
    public ResponseEntity<ResponseObject> updateJob(int jobId, UpdateJobRequest request) {
        try {
            Optional<Job> optionalJob = jobRepository.findById(jobId);
            if (optionalJob.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "Job is not exist", null));

            Job jobToUpdate = optionalJob.get();
            storageService.deleteFile(jobToUpdate.getJobImage());
            String jobImgLink = storageService.uploadFile(request.getJobImageFile());
            jobToUpdate.setJobImage(jobImgLink);
            jobToUpdate.setUpdateAt(Utils.getDateTimeNow());

            modelMapper.map(request, jobToUpdate);

            jobRepository.save(jobToUpdate);
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
            Optional<Job> optionalJob = jobRepository.findById(jobId);
            if (optionalJob.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "Job is not exist", null));

            Job jobToDelete = optionalJob.get();
            jobToDelete.setIsDelete(DeleteStatusEnum.INACTIVE.getValue());
            jobRepository.save(jobToDelete);
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
            List<Job> jobs = jobRepository.findAllActive();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Job", jobs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }
}
