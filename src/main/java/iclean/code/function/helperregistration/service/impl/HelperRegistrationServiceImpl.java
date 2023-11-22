package iclean.code.function.helperregistration.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import iclean.code.data.domain.Attachment;
import iclean.code.data.domain.HelperInformation;
import iclean.code.data.domain.ServiceRegistration;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.helperinformation.*;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.helperinformation.GetHelperInformationDetailResponse;
import iclean.code.data.dto.response.helperinformation.GetHelperInformationRequestResponse;
import iclean.code.data.dto.response.others.BackCCCD;
import iclean.code.data.dto.response.others.CMTBackResponse;
import iclean.code.data.dto.response.others.CMTFrontResponse;
import iclean.code.data.dto.response.others.FrontCCCD;
import iclean.code.data.enumjava.GenderEnum;
import iclean.code.data.enumjava.HelperStatusEnum;
import iclean.code.data.enumjava.SendMailOptionEnum;
import iclean.code.data.enumjava.ServiceHelperStatusEnum;
import iclean.code.data.repository.*;
import iclean.code.exception.BadRequestException;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.helperregistration.service.HelperRegistrationService;
import iclean.code.function.common.service.EmailSenderService;
import iclean.code.function.common.service.ExternalApiService;
import iclean.code.function.common.service.StorageService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class HelperRegistrationServiceImpl implements HelperRegistrationService {
    @Autowired
    private HelperInformationRepository helperInformationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ServiceRegistrationRepository serviceRegistrationRepository;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private ExternalApiService externalApiService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getAllRequestToBecomeHelper(Integer managerId, Boolean isAllRequest, Pageable pageable) {
        try {
            Page<HelperInformation> helpersInformation;
            if (isAllRequest) {
                helpersInformation = helperInformationRepository.findAllByStatus(HelperStatusEnum.WAITING_FOR_APPROVE, pageable);
            } else {
                helpersInformation = helperInformationRepository.findAllByStatus(managerId, HelperStatusEnum.WAITING_FOR_APPROVE, pageable);
            }
            List<GetHelperInformationRequestResponse> responses = helpersInformation
                    .stream()
                    .map(helperInformation -> modelMapper.map(helperInformation, GetHelperInformationRequestResponse.class))
                    .collect(Collectors.toList());
            PageResponseObject pageResponseObject = Utils.convertToPageResponse(helpersInformation, responses);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Helper Request List",
                            pageResponseObject));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getHelpersInformation(Integer managerId, Boolean isAllRequest, Pageable pageable) {
        try {
            Page<HelperInformation> helpersInformation;
            if (isAllRequest) {
                helpersInformation = helperInformationRepository.findAllAndOrderByStatus(pageable);
            } else {
                helpersInformation = helperInformationRepository.findAllAndOrderByStatus(managerId, pageable);
            }
            List<GetHelperInformationRequestResponse> responses = helpersInformation
                    .stream()
                    .map(helperInformation -> modelMapper.map(helperInformation, GetHelperInformationRequestResponse.class))
                    .collect(Collectors.toList());
            PageResponseObject pageResponseObject = Utils.convertToPageResponse(helpersInformation, responses);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Helpers Information List",
                            pageResponseObject));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateMoreServiceForHelper(Integer userId,
                                                                     List<MultipartFile> applications,
                                                                     List<Integer> serviceIds) {
        try {
            HelperInformation helperInformation = helperInformationRepository.findByUserId(userId);
            if (helperInformation == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                "The user not have any information about helper",
                                null));
            }
            helperInformation.setHelperStatus(HelperStatusEnum.REQUEST_MORE_SERVICE);
            helperInformationRepository.save(helperInformation);
            List<Attachment> attachments = new ArrayList<>();
            for (MultipartFile element :
                    applications) {
                Attachment attachment = new Attachment();
                String fileLink = storageService.uploadFile(element);
                attachment.setAttachmentLink(fileLink);
                attachment.setHelperInformation(helperInformation);
                attachments.add(attachment);
            }
            attachmentRepository.saveAll(attachments);
            List<ServiceRegistration> serviceRegistrations = new ArrayList<>();
            for (Integer serviceId :
                    serviceIds
            ) {
                iclean.code.data.domain.Service service = findServiceById(serviceId);
                ServiceRegistration serviceRegistration = findServiceRegistrationByUserIdAndServiceId(userId, serviceId);
                if (serviceRegistration == null) {
                    serviceRegistration = new ServiceRegistration();
                    serviceRegistration.setHelperInformation(helperInformation);
                    serviceRegistration.setService(service);
                }
                serviceRegistration.setServiceHelperStatus(ServiceHelperStatusEnum.WAITING_FOR_APPROVE);
                serviceRegistrations.add(serviceRegistration);
            }
            serviceRegistrationRepository.saveAll(serviceRegistrations);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update registration helper successful!",
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
                            "Something wrong occur!",
                            null));
        }
    }

    private void isPermission(User manager, HelperInformation helperInformation) throws UserNotHavePermissionException {
        if (!Objects.equals(helperInformation.getManagerId(), manager.getUserId())) {
            throw new UserNotHavePermissionException("You do not have permission to do this request!");
        }
    }

    @Override
    public ResponseEntity<ResponseObject> cancelHelperInformationRequest(Integer managerId, Integer id, CancelHelperRequest request) {
        try {
            User manager = findUserById(managerId);
            HelperInformation helperInformation = findHelperInformationById(id);
            isPermission(manager, helperInformation);
            helperInformation.setHelperStatus(HelperStatusEnum.DISABLED);
            helperInformation.setRejectionReason(request.getReason());
            CancelHelperRequestSendMail requestSendMail = new CancelHelperRequestSendMail(helperInformation.getEmail(),
                    helperInformation.getFullName(), request.getReason(), manager.getFullName());
            emailSenderService.sendEmailTemplate(SendMailOptionEnum.REJECT_HELPER, requestSendMail);
            helperInformationRepository.save(helperInformation);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Cancel Helper Request Successful!",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!",
                            null));
        }
    }

    private HelperInformation mappingCMTToHelperInformation(FrontCCCD frontCCCD,
                                                            BackCCCD backCCCD,
                                                            String avatarLink) {
        HelperInformation helperInformation = new HelperInformation();
        helperInformation.setPersonalAvatar(avatarLink);
        helperInformation.setFullName(frontCCCD.getName());
        helperInformation.setNationId(frontCCCD.getId());
        helperInformation.setGender(frontCCCD.getSex().equalsIgnoreCase("NAM")
                ? GenderEnum.MALE : GenderEnum.FEMALE);
        helperInformation.setDateOfBirth(Utils.convertStringToLocalDate(frontCCCD.getDob()));
        helperInformation.setDateOfIssue(Utils.convertStringToLocalDate(backCCCD.getIssueDate()));
        helperInformation.setDateOfExpired(Utils.convertStringToLocalDate(frontCCCD.getDoe()));
        helperInformation.setPlaceOfIssue(backCCCD.getIssueLoc());
        helperInformation.setPlaceOfResidence(frontCCCD.getAddress());
        helperInformation.setHomeTown(frontCCCD.getHome());
        helperInformation.setPersonalIdentification(backCCCD.getFeatures());
        return helperInformation;
    }

    private iclean.code.data.domain.Service findServiceById(Integer id) {
        return serviceRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Service is not exist!"));
    }

    private ServiceRegistration findServiceRegistrationByUserIdAndServiceId(Integer userId, Integer serviceId) {
        return serviceRegistrationRepository.findByServiceIdAndUserId(userId, serviceId);
    }

    private ServiceRegistration findById(Integer id) {
        return serviceRegistrationRepository.findById(id).orElseThrow(() -> new NotFoundException("Service Registration is not found!"));
    }

    @Override
    public ResponseEntity<ResponseObject> createHelperRegistration(HelperRegistrationRequest helperRegistrationRequest,
                                                                   Integer renterId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String imgAvatarLink = storageService.uploadFile(helperRegistrationRequest.getAvatar());
            List<Attachment> attachments = new ArrayList<>();
            String frontResponse = externalApiService.scanNationId(helperRegistrationRequest.getFrontIdCard());
            CMTFrontResponse cmtFrontResponse = objectMapper.readValue(frontResponse, CMTFrontResponse.class);
            String backResponse = externalApiService.scanNationId(helperRegistrationRequest.getBackIdCard());
            CMTBackResponse cmtBackResponse = objectMapper.readValue(backResponse, CMTBackResponse.class);
            if (!"0".equals(cmtFrontResponse.getErrorCode()) || !"0".equals(cmtBackResponse.getErrorCode())) {
                throw new BadRequestException("National ID error, please take a photo again!");
            }

            HelperInformation helperInformation = mappingCMTToHelperInformation(cmtFrontResponse.getData().get(0),
                    cmtBackResponse.getData().get(0),
                    imgAvatarLink);
            User user = findUserById(renterId);
            helperInformation.setUser(user);
            helperInformation.setEmail(helperInformation.getEmail());
            helperInformationRepository.save(helperInformation);

            List<ServiceRegistration> serviceRegistrations = new ArrayList<>();
            for (Integer serviceId :
                    helperRegistrationRequest.getServiceIds()
            ) {
                iclean.code.data.domain.Service service = findServiceById(serviceId);
                ServiceRegistration serviceRegistration = new ServiceRegistration();
                serviceRegistration.setHelperInformation(helperInformation);
                serviceRegistration.setService(service);
                serviceRegistration.setServiceHelperStatus(ServiceHelperStatusEnum.WAITING_FOR_APPROVE);
                serviceRegistrations.add(serviceRegistration);
            }
            serviceRegistrationRepository.saveAll(serviceRegistrations);

            List<MultipartFile> attachmentRequest = helperRegistrationRequest.getOthers();
            attachmentRequest.addAll(List.of(helperRegistrationRequest.getFrontIdCard(),
                    helperRegistrationRequest.getBackIdCard()));
            for (MultipartFile element :
                    attachmentRequest) {
                Attachment attachment = new Attachment();
                String fileLink = storageService.uploadFile(element);
                attachment.setAttachmentLink(fileLink);
                attachment.setHelperInformation(helperInformation);
                attachments.add(attachment);
            }
            attachmentRepository.saveAll(attachments);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Register become helper successful - please waiting for our response email!",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof BadRequestException) {
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                e.getMessage(),
                                null));
            }
            if (e instanceof NotFoundException) {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getHelperInformation(Integer id) {
        try {
            HelperInformation helperInformation = findHelperInformationById(id);
            GetHelperInformationDetailResponse response = modelMapper.map(helperInformation, GetHelperInformationDetailResponse.class);
            List<String> attachments = helperInformation.getAttachments()
                    .stream()
                    .map(Attachment::getAttachmentLink).collect(Collectors.toList());
            response.setAttachments(attachments);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Helper Information Detail",
                            response));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> acceptHelperInformation(Integer managerId, Integer id) {
        try {
            HelperInformation helperInformation = findHelperInformationById(id);
            helperInformation.setHelperStatus(HelperStatusEnum.WAITING_FOR_CONFIRM);
            User manager = findUserById(managerId);
            isPermission(manager, helperInformation);
            LocalDateTime current = Utils.getLocalDateTimeNow();
            LocalDateTime meetingDateTime = current.plusDays(7);
            List<HelperInformation> helperInformationOptional = helperInformationRepository
                    .findMaxByMeetingDateTimeAndHelperStatus(HelperStatusEnum.WAITING_FOR_APPROVE);
            if (!helperInformationOptional.isEmpty()) {
                if (current.isBefore(helperInformationOptional.get(0).getMeetingDateTime())) {
                    LocalDateTime startTime = helperInformationOptional.get(0).getMeetingDateTime().toLocalDate().atStartOfDay();
                    LocalDateTime endTime = startTime.plusDays(1);
                    Integer count = helperInformationRepository.findAllByMeetingDatetime(startTime, endTime, HelperStatusEnum.WAITING_FOR_CONFIRM);
                    if (count >= getMaxRequestADay()) {
                        meetingDateTime = endTime.plusHours(8);
                    } else {
                        meetingDateTime = helperInformationOptional.get(0).getMeetingDateTime().toLocalDate().atStartOfDay().plusHours(8);
                    }
                }
            }
            helperInformation.setMeetingDateTime(meetingDateTime);
            AcceptHelperRequest request = new AcceptHelperRequest(helperInformation.getEmail(), helperInformation.getFullName(), manager.getFullName(),
                    meetingDateTime);
            emailSenderService.sendEmailTemplate(SendMailOptionEnum.ACCEPT_HELPER, request);

            helperInformationRepository.save(helperInformation);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Accept Helper Information Successful!",
                            "Meeting Time: " + meetingDateTime));
        } catch (Exception e) {
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!",
                            null));
        }
    }

    private Integer getMaxRequestADay() {
        return 10;
    }

    @Override
    public ResponseEntity<ResponseObject> confirmHelperInformation(Integer managerId, Integer id, ConfirmHelperRequest request) {
        try {
            try {
                HelperInformation helperInformation = findHelperInformationById(id);
                User manager = findUserById(managerId);
                isPermission(manager, helperInformation);
                helperInformation.setHelperStatus(HelperStatusEnum.ONLINE);
                List<ServiceRegistration> serviceRegistrations = new ArrayList<>();
                List<String> serviceNames = new ArrayList<>();
                for (Integer requestService :
                        request.getServiceRegistrationIds()) {
                    ServiceRegistration serviceRegistration = findById(requestService);
                    serviceRegistration.setServiceHelperStatus(ServiceHelperStatusEnum.ACTIVE);
                    serviceRegistrations.add(serviceRegistration);
                    serviceNames.add(serviceRegistration.getService().getServiceName());
                }
                ConfirmHelperRequestSendMail requestSendMail = new ConfirmHelperRequestSendMail(helperInformation.getEmail(),
                        serviceNames, helperInformation.getFullName(),
                        manager.getFullName());
                serviceRegistrationRepository.saveAll(serviceRegistrations);
                emailSenderService.sendEmailTemplate(SendMailOptionEnum.CONFIRM_HELPER, requestSendMail);

                helperInformationRepository.save(helperInformation);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(),
                                "Confirm Helper Information Successful!",
                                null));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                "Something wrong occur!",
                                null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!",
                            null));
        }
    }

    private User findUserById(Integer id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User ID: %s is not exist", id)));
    }

    private HelperInformation findHelperInformationById(Integer id) {
        return helperInformationRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Helper Information ID: %s is not exist", id)));
    }
}
