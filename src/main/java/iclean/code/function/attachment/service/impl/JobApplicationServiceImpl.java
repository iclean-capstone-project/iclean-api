package iclean.code.function.attachment.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import iclean.code.data.domain.Attachment;
import iclean.code.data.domain.HelperInformation;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.attachment.CreateAttachmentRequestDTO;
import iclean.code.data.dto.request.attachment.UpdateAttachmentRequestDTO;
import iclean.code.data.dto.response.others.CMTBackResponse;
import iclean.code.data.dto.response.others.CMTFrontResponse;
import iclean.code.data.repository.AttachmentRepository;
import iclean.code.data.repository.HelperInformationRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.attachment.service.JobApplicationService;
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
    private AttachmentRepository attachmentRepository;
    @Autowired
    private HelperInformationRepository helperInformationRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ExternalApiService externalApiService;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getJobApplications() {
        try {
            List<Attachment> attachments = attachmentRepository.findAll();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Job Application Detail",
                            attachments));
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
            Attachment attachment = findJobApplication(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Job Application Detail",
                            attachment));
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
    public ResponseEntity<ResponseObject> createJobApplication(CreateAttachmentRequestDTO request,
                                                               MultipartFile frontIdCard,
                                                               MultipartFile backIdCard,
                                                               MultipartFile avatar,
                                                               List<MultipartFile> others) {
        try {
            String imgAvatarLink = storageService.uploadFile(avatar);
            Attachment attachment = new Attachment();
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

            HelperInformation helperInformation = new HelperInformation();
//            registerEmployee.setPhoneNumber(request.getPhoneNumber());
            helperInformation.setNationId(cmtFrontResponse.getData().get(0).getId());
            helperInformation.setFullName(cmtFrontResponse.getData().get(0).getName());
            attachment.setAttachmentLink(imgAvatarLink);
            attachment.setCreateAt(Utils.getDateTimeNow());
//            jobApplication.setRegisterEmployee(findRegisterEmployee(request.getRegisterEmployeeId()));

            attachmentRepository.save(attachment);

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
    public ResponseEntity<ResponseObject> updateJobApplication(Integer id, UpdateAttachmentRequestDTO request, MultipartFile file) {
        try {
            Attachment attachment = findJobApplication(id);
            storageService.deleteFile(attachment.getAttachmentLink());
            String imgLink = storageService.uploadFile(file);
            attachment = modelMapper.map(request, Attachment.class);
            attachment.setAttachmentLink(imgLink);
            attachment.setCreateAt(Utils.getDateTimeNow());
            attachment.setHelperInformation(findRegisterEmployee(request.getRegisterEmployeeId()));

            attachmentRepository.save(attachment);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update a Job Application Successful",
                            attachment));
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
            Attachment attachment = findJobApplication(id);
            storageService.deleteFile(attachment.getAttachmentLink());

            attachmentRepository.delete(attachment);

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

    private Attachment findJobApplication(int id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Job Application ID: %s is not exist", id)));
    }

    private HelperInformation findRegisterEmployee(int id) {
        return helperInformationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Register Employee ID: %s is not exist", id)));
    }
}
