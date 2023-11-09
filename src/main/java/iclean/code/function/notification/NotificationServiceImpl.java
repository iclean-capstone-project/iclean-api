package iclean.code.function.notification;

import iclean.code.data.domain.*;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.notification.GetNotificationDTO;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.enumjava.NotificationEnum;
import iclean.code.data.enumjava.NotificationStatusEnum;
import iclean.code.data.enumjava.RoleEnum;
import iclean.code.data.repository.NotificationRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.notification.service.NotificationService;
import iclean.code.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getNotificationById(Integer notificationId, Integer userId) {
        try {
            GetNotificationDTO notificationResponse = null;
            Notification notification = findNotification(notificationId);
            if (isPermission(userId, notification)) {
                notificationResponse = modelMapper.map(notification, GetNotificationDTO.class);
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Notification", notificationResponse));

        } catch (Exception e) {
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getNotifications(Integer userIdAuth, Pageable pageable) {
        try {
            Page<Notification> notifications;
            Sort order = Sort.by(Sort.Order.desc("createAt"));
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), order);
            String roleUser = userRepository.findByUserId(userIdAuth).getRole().getTitle().toUpperCase();

            if (Utils.isNullOrEmpty(roleUser))
                throw new UserNotHavePermissionException("User do not have permission to do this action");
            RoleEnum roleEnum = RoleEnum.valueOf(roleUser);
            switch (roleEnum) {
                case EMPLOYEE:
                    notifications = notificationRepository.findByUserIdPageable(userIdAuth, Boolean.parseBoolean(NotificationEnum.IS_EMPLOYEE.toString()), pageable);
                    break;
                case RENTER:
                    notifications = notificationRepository.findByUserIdPageable(userIdAuth, Boolean.parseBoolean(NotificationEnum.NOT_EMPLOYEE.toString()), pageable);
                    break;
                default:
                    notifications = notificationRepository.findByUserIdPageable(userIdAuth, pageable);
            }

            List<GetNotificationDTO> dtoList = notifications
                    .stream()
                    .map(notificationMapper -> modelMapper.map(notificationMapper, GetNotificationDTO.class))
                    .collect(Collectors.toList());

            PageResponseObject pageResponseObject = Utils.convertToPageResponse(notifications, dtoList);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Notification", pageResponseObject));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateStatusNotification(int notificationId, int userId) {
        try {
            Notification notification = mappingNotificationForUpdate(notificationId);
            notificationRepository.save(notification);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update Notification Successfully!", null));

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
    public ResponseEntity<ResponseObject> deleteNotification(int notificationId, int userId) {
        try {
            Notification notification = findNotification(notificationId);
            if (isPermission(userId, notification)) {
                notificationRepository.delete(notification);
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Delete Notification Successfully!", null));
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
    public ResponseEntity<ResponseObject> readAllNotification(int userId) {
        try {
            List<Notification> notifications;
            String roleUser = userRepository.findByUserId(userId).getRole().getTitle().toUpperCase();

            if (Utils.isNullOrEmpty(roleUser))
                throw new UserNotHavePermissionException("User do not have permission to do this action");
            RoleEnum roleEnum = RoleEnum.valueOf(roleUser);
            switch (roleEnum) {
                case EMPLOYEE:
                    notifications = notificationRepository.findAllByUserIdAndRead(userId, false, Boolean.parseBoolean(NotificationEnum.IS_EMPLOYEE.toString()));
                    break;
                case RENTER:
                    notifications = notificationRepository.findAllByUserIdAndRead(userId, false, Boolean.parseBoolean(NotificationEnum.NOT_EMPLOYEE.toString()));
                    break;
                default:
                    notifications = notificationRepository.findAllByUserIdAndRead(userId, false);
            }
            for (Notification notification :
                    notifications) {
                notification.setIsRead(true);
            }
            notificationRepository.saveAll(notifications);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update Notification Successfully!", null));

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

    private Notification mappingNotificationForUpdate(int notificationId) {
        Notification optionalNotification = findNotification(notificationId);
        optionalNotification.setIsRead(NotificationStatusEnum.READ.isValue());
        return modelMapper.map(optionalNotification, Notification.class);
    }

    private Notification findNotification(int notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification is not exist"));
    }

    private boolean isPermission(Integer userId, Notification notification) throws UserNotHavePermissionException {
        if (!Objects.equals(notification.getUser().getUserId(), userId))
            throw new UserNotHavePermissionException("User do not have permission to do this action");
        return true;
    }
}
