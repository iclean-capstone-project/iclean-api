package iclean.code.function.job.service.impl;

import iclean.code.data.domain.Job;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.job.AddJobRequest;
import iclean.code.data.dto.request.job.UpdateJobRequest;
import iclean.code.data.repository.JobRepository;
import iclean.code.function.job.service.JobService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@Log4j2
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Validator validator;

    @Override
    public ResponseEntity<ResponseObject> getAllJob() {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ResponseObject(HttpStatus.ACCEPTED.toString(), "All Job", jobRepository.finAll()));
    }

    @Override
    public ResponseEntity<ResponseObject> addJob(AddJobRequest request) {
        try {
            Set<ConstraintViolation<AddJobRequest>> violations = validator.validate(request);

            if (!violations.isEmpty()) {
                // Handle validation errors (e.g., return error response)
                throw new IllegalArgumentException("Validation error: " + violations.iterator().next().getMessage());
            }

            Job job = modelMapper.map(request, Job.class);
            job.setCreateAt(LocalDateTime.now());

            jobRepository.save(job);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(HttpStatus.ACCEPTED.toString()
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
            Set<ConstraintViolation<UpdateJobRequest>> violations = validator.validate(newJob);

            if (!violations.isEmpty()) {
                // Handle validation errors (e.g., return error response)
                throw new IllegalArgumentException("Validation error: " + violations.iterator().next().getMessage());
            }

            Optional<Job> optionalJob = jobRepository.findById(jobId);
            if (optionalJob.isEmpty())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Job is not exist", null));

            Job jobToUpdate = optionalJob.get();
            jobToUpdate.setJobName(newJob.getJobName());
            jobToUpdate.setJobImage(newJob.getJobImage());
            jobToUpdate.setDescription(newJob.getDescription());
            jobToUpdate.setUpdateAt(LocalDateTime.now());

            Job job = modelMapper.map(jobToUpdate, Job.class);

            jobRepository.save(job);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(HttpStatus.ACCEPTED.toString()
                            , "Update Job Successfully!", null));

        } catch (Exception e) {
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Job is not exist", null));

            Job jobToDelete = optionalJob.get();
            jobRepository.delete(jobToDelete);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(HttpStatus.ACCEPTED.toString()
                            , "Delete Job Successfully!", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }
}
