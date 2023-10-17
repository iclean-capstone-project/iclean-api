package iclean.code.function.jobapplication.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import iclean.code.data.domain.JobApplication;
import iclean.code.data.domain.RegisterEmployee;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.jobapplication.CreateJobApplicationRequestDTO;
import iclean.code.data.dto.request.jobapplication.UpdateJobApplicationRequestDTO;
import iclean.code.data.dto.response.others.CMTBackResponse;
import iclean.code.data.dto.response.others.CMTFrontResponse;
import iclean.code.data.repository.JobApplicationRepository;
import iclean.code.data.repository.RegisterEmployeeRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.jobapplication.service.JobApplicationService;
import iclean.code.service.ExternalApiService;
import iclean.code.service.StorageService;
import iclean.code.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    private ExternalApiService externalApiService;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getJobApplications() {
        try {
            List<JobApplication> jobApplications = jobApplicationRepository.findAll();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Job Application Detail",
                            jobApplications));
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
    public ResponseEntity<ResponseObject> createJobApplication(CreateJobApplicationRequestDTO request,
                                                               MultipartFile frontIdCard,
                                                               MultipartFile backIdCard,
                                                               MultipartFile avatar,
                                                               List<MultipartFile> others) {
        try {
            String imgAvatarLink = storageService.uploadFile(avatar);
            JobApplication jobApplication = new JobApplication();
//            String frontResponse = externalApiService.scanNationId(frontIdCard);
            String frontResponse = "{\n" +
                    "    \"errorCode\": 0,\n" +
                    "    \"errorMessage\": \"\",\n" +
                    "    \"data\": [\n" +
                    "        {\n" +
                    "            \"id\": \"075201010458\",\n" +
                    "            \"id_prob\": \"98.31\",\n" +
                    "            \"name\": \"NGUYỄN PHƯƠNG NHẬT LINH\",\n" +
                    "            \"name_prob\": \"99.73\",\n" +
                    "            \"dob\": \"18/11/2001\",\n" +
                    "            \"dob_prob\": \"99.47\",\n" +
                    "            \"sex\": \"NAM\",\n" +
                    "            \"sex_prob\": \"99.15\",\n" +
                    "            \"nationality\": \"VIỆT NAM\",\n" +
                    "            \"nationality_prob\": \"98.29\",\n" +
                    "            \"home\": \"NHẬT TÂN, KIM BẢNG, HÀ NAM\",\n" +
                    "            \"home_prob\": \"99.22\",\n" +
                    "            \"address\": \"TỔ 12 ẤP 1, VĨNH TÂN, VĨNH CỬU, ĐỒNG NAI\",\n" +
                    "            \"address_prob\": \"98.49\",\n" +
                    "            \"doe\": \"18/11/2026\",\n" +
                    "            \"doe_prob\": \"99.17\",\n" +
                    "            \"overall_score\": \"99.46\",\n" +
                    "            \"address_entities\": {\n" +
                    "                \"province\": \"ĐỒNG NAI\",\n" +
                    "                \"district\": \"VĨNH CỬU\",\n" +
                    "                \"ward\": \"VĨNH TÂN\",\n" +
                    "                \"street\": \"TỔ 12 ẤP 1\"\n" +
                    "            },\n" +
                    "            \"type_new\": \"cccd_chip_front\",\n" +
                    "            \"type\": \"chip_front\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
            ObjectMapper objectMapper = new ObjectMapper();
            CMTFrontResponse cmtFrontResponse = objectMapper.readValue(frontResponse, CMTFrontResponse.class);
            String backResponse = "{\n" +
                    "    \"errorCode\": 0,\n" +
                    "    \"errorMessage\": \"\",\n" +
                    "    \"data\": [\n" +
                    "        {\n" +
                    "            \"features\": \"NỐT RUỒI C:5CM DƯỚI TRƯỚC ĐẦU MÀY PHẢI\",\n" +
                    "            \"features_prob\": \"98.21\",\n" +
                    "            \"issue_date\": \"12/08/2021\",\n" +
                    "            \"issue_date_prob\": \"99.36\",\n" +
                    "            \"mrz\": [\n" +
                    "                \"IDVNM2010104589075201010458<<5\",\n" +
                    "                \"0111182M2611181VNM<<<<<<<<<<<0\",\n" +
                    "                \"NGUYEN<<PHUONG<NHAT<LINH<<<<<<\"\n" +
                    "            ],\n" +
                    "            \"mrz_prob\": \"94.58\",\n" +
                    "            \"overall_score\": \"99.56\",\n" +
                    "            \"issue_loc\": \"CỤC CẢNH SÁT QUẢN LÝ HÀNH CHÍNH VỀ TRẬT TỰ XÃ HỘI\",\n" +
                    "            \"issue_loc_prob\": \"95.9\",\n" +
                    "            \"type_new\": \"chip_back\",\n" +
                    "            \"type\": \"chip_back\",\n" +
                    "            \"mrz_details\": {\n" +
                    "                \"id\": \"075201010458\",\n" +
                    "                \"name\": \"NGUYEN PHUONG NHAT LINH\",\n" +
                    "                \"doe\": \"18/11/2026\",\n" +
                    "                \"dob\": \"18/11/2001\",\n" +
                    "                \"nationality\": \"VNM\",\n" +
                    "                \"sex\": \"M\"\n" +
                    "            }\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
            CMTBackResponse cmtBackResponse = objectMapper.readValue(backResponse, CMTBackResponse.class);

            RegisterEmployee registerEmployee = new RegisterEmployee();
//            registerEmployee.setPhoneNumber(request.getPhoneNumber());
            registerEmployee.setNationId(cmtFrontResponse.getData().get(0).getId());
            registerEmployee.setFullName(cmtFrontResponse.getData().get(0).getName());
            jobApplication.setJobImgLink(imgAvatarLink);
            jobApplication.setCreateAt(Utils.getDateTimeNow());
//            jobApplication.setRegisterEmployee(findRegisterEmployee(request.getRegisterEmployeeId()));

            jobApplicationRepository.save(jobApplication);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create new Job Application Successful",
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
