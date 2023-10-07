package iclean.code.function.job.service.impl;

import iclean.code.data.domain.Job;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.job.CreateJobRequest;
import iclean.code.data.dto.request.job.UpdateJobRequest;
import iclean.code.data.repository.JobRepository;
import iclean.code.data.repository.JobUnitRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Log4j2
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobUnitRepository jobUnitRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getJobs() {
        if (jobRepository.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(), "All Job", "Job list is empty"));
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ResponseObject(HttpStatus.ACCEPTED.toString(), "All Job", jobRepository.finAll()));
    }

    @Override
    public ResponseEntity<ResponseObject> createJob(CreateJobRequest request, MultipartFile imgJob) {
        try {
            Job job = modelMapper.map(request, Job.class);
            String jobImgLink = storageService.uploadFile(imgJob);
            job.setJobImage(jobImgLink);
            job.setCreateAt(Utils.getDateTimeNow());

            jobRepository.save(job);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Create Job Successfully!", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateJob(int jobId, UpdateJobRequest newJob) {
        try {
            Optional<Job> optionalJob = jobRepository.findById(jobId);
            if (optionalJob.isEmpty())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Job is not exist", null));

            Job jobToUpdate = optionalJob.get();
            jobToUpdate.setJobName(newJob.getJobName());
            jobToUpdate.setJobImage(newJob.getJobImage());
            jobToUpdate.setDescription(newJob.getDescription());
            jobToUpdate.setUpdateAt(Utils.getDateTimeNow());

            Job job = modelMapper.map(jobToUpdate, Job.class);

            jobRepository.save(job);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update Job Successfully!", null));

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
                                , "Something wrong occur!", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
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
            jobRepository.delete(jobToDelete);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(HttpStatus.ACCEPTED.toString()
                            , "Delete Job Successfully!", null));

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
                                , "Something wrong occur!", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }
}
