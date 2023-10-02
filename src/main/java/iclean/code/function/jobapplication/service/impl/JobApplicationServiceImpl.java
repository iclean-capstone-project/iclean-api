package iclean.code.function.jobapplication.service.impl;

import iclean.code.data.domain.JobApplication;
import iclean.code.data.domain.RegisterEmployee;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.jobapplication.CreateJobApplicationRequestDTO;
import iclean.code.data.dto.request.jobapplication.GetJobApplicationRequestDTO;
import iclean.code.data.dto.request.jobapplication.UpdateJobApplicationRequestDTO;
import iclean.code.data.repository.JobApplicationRepository;
import iclean.code.data.repository.RegisterEmployeeRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.jobapplication.service.JobApplicationService;
import iclean.code.service.StorageService;
import iclean.code.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class JobApplicationServiceImpl implements JobApplicationService {
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    @Autowired
    private RegisterEmployeeRepository registerEmployeeRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getJobApplications(GetJobApplicationRequestDTO request) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> getJobApplication(Integer id) {
        try {
            JobApplication jobApplication = findJobApplication(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Job Application Detail",
                            jobApplication));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createJobApplication(CreateJobApplicationRequestDTO request, MultipartFile file) {
        try {
            String imgLink = storageService.uploadFile(file);
            JobApplication jobApplication = modelMapper.map(request, JobApplication.class);
            jobApplication.setJobImgLink(imgLink);
            jobApplication.setCreateAt(Utils.getDateTimeNow());
            jobApplication.setRegisterEmployee(findRegisterEmployee(request.getRegisterEmployeeId()));

            jobApplicationRepository.save(jobApplication);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create new Job Application Successful",
                            jobApplication));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateJobApplication(Integer id, UpdateJobApplicationRequestDTO request, MultipartFile file) {
        try {
            JobApplication jobApplication = findJobApplication(id);
            storageService.deleteFile(jobApplication.getJobImgLink());
            String imgLink = storageService.uploadFile(file);
            jobApplication = modelMapper.map(request, JobApplication.class);
            jobApplication.setJobImgLink(imgLink);
            jobApplication.setCreateAt(Utils.getDateTimeNow());
            jobApplication.setRegisterEmployee(findRegisterEmployee(request.getRegisterEmployeeId()));

            jobApplicationRepository.save(jobApplication);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update a Job Application Successful",
                            jobApplication));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteJobApplication(Integer id) {
        try {
            JobApplication jobApplication = findJobApplication(id);
            storageService.deleteFile(jobApplication.getJobImgLink());

            jobApplicationRepository.delete(jobApplication);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete a Job Application Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    private JobApplication findJobApplication(int id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Job Application ID: %s is not exist", id)));
    }

    private RegisterEmployee findRegisterEmployee(int id) {
        return registerEmployeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Register Employee ID: %s is not exist", id)));
    }
}
