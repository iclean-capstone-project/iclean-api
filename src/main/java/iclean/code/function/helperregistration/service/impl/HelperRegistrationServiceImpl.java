package iclean.code.function.helperregistration.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import iclean.code.config.MessageVariable;
import iclean.code.data.domain.*;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.helperinformation.*;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.helperinformation.GetHelperInformationDetailResponse;
import iclean.code.data.dto.response.helperinformation.GetHelperInformationRequestResponse;
import iclean.code.data.dto.response.others.BackCCCD;
import iclean.code.data.dto.response.others.CMTBackResponse;
import iclean.code.data.dto.response.others.CMTFrontResponse;
import iclean.code.data.dto.response.others.FrontCCCD;
import iclean.code.data.dto.response.service.GetServiceResponse;
import iclean.code.data.dto.response.serviceregistration.GetServiceOfHelperResponse;
import iclean.code.data.enumjava.*;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
    private RoleRepository roleRepository;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private ExternalApiService externalApiService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getAllRequestToBecomeHelper(Integer managerId, Boolean isAllRequest, String startDate, String endDate, List<String> statuses, Pageable pageable) {
        try {
            Page<HelperInformation> helpersInformation;
            List<HelperStatusEnum> helperStatusEnums = null;
            if (!(statuses == null || statuses.isEmpty())) {
                helperStatusEnums = statuses
                        .stream()
                        .map(element -> HelperStatusEnum.valueOf(element.toUpperCase()))
                        .collect(Collectors.toList());
            }
            if (isAllRequest) {
                helpersInformation = !(statuses == null || statuses.isEmpty())
                        ? helperInformationRepository.findAllByStatus(helperStatusEnums, pageable)
                        : helperInformationRepository.findAll(pageable);
            } else {
                helpersInformation = !(statuses == null || statuses.isEmpty())
                        ? helperInformationRepository.findAllByStatus(managerId, helperStatusEnums, pageable)
                        : helperInformationRepository.findAllByManagerId(managerId, pageable);
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
                    .map(helperInformation -> {
                        GetHelperInformationRequestResponse response = modelMapper.map(helperInformation, GetHelperInformationRequestResponse.class);
                        response.setPersonalAvatar(helperInformation.getUser().getAvatar());
                        return response;
                    })
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
//            isPermission(manager, helperInformation);
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
        helperInformation.setDateOfBirth(Utils.convertStringToLocalDateCMT(frontCCCD.getDob()));
        helperInformation.setDateOfIssue(Utils.convertStringToLocalDateCMT(backCCCD.getIssueDate()));
        helperInformation.setDateOfExpired(Utils.convertStringToLocalDateCMT(frontCCCD.getDoe()));
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
            HelperInformation helperInformationCheck = helperInformationRepository.findByUserId(renterId);
            if (Objects.nonNull(helperInformationCheck)) {
                throw new BadRequestException(MessageVariable.CANNOT_REGISTER_HELPER_ALREADY_HAVE);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String imgAvatarLink = null;
            if (helperRegistrationRequest.getAvatar() != null) {
                imgAvatarLink = storageService.uploadFile(helperRegistrationRequest.getAvatar());
            }
            List<Attachment> attachments = new ArrayList<>();

            Attachment frontCMT = new Attachment();
            String frontCMTLink = storageService.uploadFile(helperRegistrationRequest.getFrontIdCard());
            Attachment backCMT = new Attachment();
            String backCMTLink = storageService.uploadFile(helperRegistrationRequest.getBackIdCard());

            String frontResponse = externalApiService.scanNationId(helperRegistrationRequest.getFrontIdCard());
            CMTFrontResponse cmtFrontResponse = objectMapper.readValue(frontResponse, CMTFrontResponse.class);
            String backResponse = externalApiService.scanNationId(helperRegistrationRequest.getBackIdCard());
            CMTBackResponse cmtBackResponse = objectMapper.readValue(backResponse, CMTBackResponse.class);
            if (!"0".equals(cmtFrontResponse.getErrorCode()) || !"0".equals(cmtBackResponse.getErrorCode())) {
                throw new BadRequestException("National ID error, please take a photo again!");
            }
            HelperInformation existedHelperInfo = helperInformationRepository.findByNationId(cmtFrontResponse.getData().get(0).getId());
            if (!Objects.isNull(existedHelperInfo))
                throw new BadRequestException("National ID existed in system, please take a other photo again!");
            HelperInformation helperInformation = mappingCMTToHelperInformation(cmtFrontResponse.getData().get(0),
                    cmtBackResponse.getData().get(0),
                    imgAvatarLink);
            if (Utils.getLocalDateTimeNow().toLocalDate().minusYears(18).isBefore(helperInformation.getDateOfBirth())) {
                throw new BadRequestException(MessageVariable.CANNOT_REGISTER_HELPER_NOT_ENOUGH_AGE);
            }
            User user = findUserById(renterId);
            helperInformation.setUser(user);
            helperInformation.setPhoneNumber(user.getPhoneNumber());
            helperInformation.setEmail(helperRegistrationRequest.getEmail());
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

            Attachment avatar = new Attachment();
            if (!Utils.isNullOrEmpty(imgAvatarLink)) {
                avatar.setAttachmentLink(imgAvatarLink);
                avatar.setHelperInformation(helperInformation);
                attachments.add(avatar);
            }

            if (!Utils.isNullOrEmpty(frontCMTLink)) {
                frontCMT.setAttachmentLink(frontCMTLink);
                frontCMT.setHelperInformation(helperInformation);
                attachments.add(frontCMT);
            }

            if (!Utils.isNullOrEmpty(backCMTLink)) {
                backCMT.setAttachmentLink(backCMTLink);
                backCMT.setHelperInformation(helperInformation);
                attachments.add(backCMT);
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
            if(HelperStatusEnum.ONLINE.equals(helperInformation.getHelperStatus())){
                List<ServiceRegistration> filteredList = helperInformation.getServiceRegistrations().stream()
                        .filter(serviceRegistration -> serviceRegistration.getServiceHelperStatus().equals(ServiceHelperStatusEnum.ACTIVE))
                        .collect(Collectors.toList());
                List<GetServiceOfHelperResponse> serviceOfHelperResponses = filteredList.stream()
                        .map(service -> modelMapper.map(service, GetServiceOfHelperResponse.class))
                        .collect(Collectors.toList());
                response.setServices(serviceOfHelperResponses);
            }
            response.setPersonalAvatar(helperInformation.getUser().getAvatar());
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
            helperInformation.setManagerId(managerId);
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

    private Role getRoleHelper() {
        return roleRepository.findByTitle(RoleEnum.EMPLOYEE.name().toLowerCase());
    }

    @Override
    public ResponseEntity<ResponseObject> confirmHelperInformation(Integer managerId, Integer id, ConfirmHelperRequest request) {
        try {
            HelperInformation helperInformation = findHelperInformationById(id);
            User manager = findUserById(managerId);
//            isPermission(manager, helperInformation);
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
            List<ServiceRegistration> serviceRegistrationRequests = serviceRegistrationRepository.findAllByHelperId(id);
            List<ServiceRegistration> filterServiceRequests = serviceRegistrationRequests.stream()
                    .filter(serviceRegistration -> !request.getServiceRegistrationIds().contains(serviceRegistration.getServiceRegistrationId()))
                    .collect(Collectors.toList());
            for (ServiceRegistration serviceRegistration :
                    filterServiceRequests) {
                serviceRegistration.setServiceHelperStatus(ServiceHelperStatusEnum.DISABLED);
            }
            User user = helperInformation.getUser();
            user.setRoleId(getRoleHelper().getRoleId());
            ConfirmHelperRequestSendMail requestSendMail = new ConfirmHelperRequestSendMail(helperInformation.getEmail(),
                    serviceNames, helperInformation.getFullName(),
                    manager.getFullName());
            serviceRegistrationRepository.saveAll(filterServiceRequests);
            serviceRegistrationRepository.saveAll(serviceRegistrations);
            emailSenderService.sendEmailTemplate(SendMailOptionEnum.CONFIRM_HELPER, requestSendMail);
            helperInformationRepository.save(helperInformation);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Confirm Helper Information Successful!",
                            null));
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            }
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
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
    public ResponseEntity<ResponseObject> assignManageToRegistration() {
        try{
            List<HelperInformation> helperInformations = helperInformationRepository.findAllHelperInformationHaveNoManager();
            if(helperInformations.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                Utils.getDateTimeNowAsString() + " ---->> All registration already manage",
                                null));
            }
            List<User> managers = userRepository.findAllManager();
            if (managers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                Utils.getDateTimeNowAsString() + " ---->> No manager at this time!",
                                null));
            }
            int countManager = managers.size();
            int i = 0;
            for (HelperInformation helperInformation :
                    helperInformations
            ) {
                helperInformation.setManagerId(managers.get(i++).getUserId());
                if (i == countManager) i = 0;
            }

            helperInformationRepository.saveAll(helperInformations);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Set manager for registration successfully",
                            null));
        } catch (Exception ex){
            if (ex instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                ex.getMessage(),
                                null));
            }
            if (ex instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                ex.getMessage(),
                                null));
            }
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
