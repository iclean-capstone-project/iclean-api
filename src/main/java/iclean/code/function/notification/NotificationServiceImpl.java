package iclean.code.function.notification;

import iclean.code.data.domain.*;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.notification.AddNotificationRequest;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.enumjava.Role;
import iclean.code.data.enumjava.StatusNotification;
import iclean.code.data.repository.NotificationRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.notification.service.NotificationService;
import iclean.code.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getAllNotification(Integer userId, Pageable pageable) {
        Page<Notification> notifications = null;
        User user = findUser(userId);
        if (Objects.equals(iclean.code.data.enumjava.Role.RENTER.toString(), user.getRole().getTitle())
                || Objects.equals(Role.EMPLOYEE.toString(), user.getRole().getTitle())) {
            notifications = notificationRepository.findByUserIdPageable(userId, pageable);
        } else {
            notifications = notificationRepository.findAll(pageable);
        }
        PageResponseObject pageResponseObject = Utils.convertToPageResponse(notifications);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString()
                        , "All Notification", pageResponseObject));
    }

    @Override
    public ResponseEntity<ResponseObject> getNotificationById(Integer notificationId, Integer userId, Pageable pageable) {
        try {
            Notification notification = findNotification(notificationId);
            if(isAuthorized(userId, notification)) {
                if(!Objects.equals(userId, notification.getUser().getUserId())){
                    throw new UserNotHavePermissionException();
                }
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString()
                                , "Notification", notificationRepository.findById(notificationId)));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Notification",notificationRepository.findById(notificationId)));

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
    public ResponseEntity<ResponseObject> getNotificationByUserId(Integer userId, Integer userIdAuth, Pageable pageable) {
        try {
            Notification notification = findNotification(userId);
            if (!Objects.equals(notification.getUser().getUserId(), userIdAuth))
                throw new UserNotHavePermissionException();

            Page<Notification> notifications = notificationRepository.findByUserIdPageable(userIdAuth, pageable);
            PageResponseObject pageResponseObject = Utils.convertToPageResponse(notifications);

            if (notificationRepository.findNotificationByUserUserId(userId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
                                , "Notification", "User's notification list is empty"));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Notification", pageResponseObject));
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
    public ResponseEntity<ResponseObject> addNotification(AddNotificationRequest request) {
        try {

            Notification notification = mappingNotificationForCreate(request);
            notificationRepository.save(notification);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Create Notification Successfully!", null));

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
    public ResponseEntity<ResponseObject> updateStatusNotification(int notificationId) {
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
    public ResponseEntity<ResponseObject> deleteNotification(int notificationId) {
        try {
            Notification notification = findNotification(notificationId);
            notificationRepository.delete(notification);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject(HttpStatus.ACCEPTED.toString()
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

    private Notification mappingNotificationForCreate(AddNotificationRequest request) {
        User optionalUser = findUser(request.getUserId());

        Notification notification = modelMapper.map(request, Notification.class);
        notification.setContent(request.getContent());
        notification.setTitle(request.getTitle());
        notification.setCreateAt(Utils.getDateTimeNow());
        notification.setUser(optionalUser);
        notification.setStatus(StatusNotification.NOT_READ.getValue());
        notification.setNotificationImgLink(request.getNotificationImgLink());

        return notification;
    }

    private Notification mappingNotificationForUpdate(int notificationId) {

        Notification optionalNotification = findNotification(notificationId);

        optionalNotification.setStatus(StatusNotification.READ.getValue());


        return modelMapper.map(optionalNotification, Notification.class);
    }

    private Notification findNotification(int notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification is not exist"));
    }

    private User findUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not exist"));
    }

    private boolean isAuthorized(Integer userId, Notification notification) {
        User user = findUser(userId);
        if (Role.RENTER.toString().equals(user.getRole().getTitle()) || Role.EMPLOYEE.toString().equals(user.getRole().getTitle())) {
            return Objects.equals(user.getUserId(), notification.getUser().getUserId());
        }
        return true;
    }
}
