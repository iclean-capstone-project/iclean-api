package iclean.code.function.notification.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.notification.AddNotificationRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    ResponseEntity<ResponseObject> getAllNotification(Integer userId, Pageable pageable);

    ResponseEntity<ResponseObject> getNotificationById(Integer notificationId, Integer userId, Pageable pageable);

    ResponseEntity<ResponseObject> getNotificationByUserId(Integer userId, Integer userIdAuth, Pageable pageable);

    ResponseEntity<ResponseObject> addNotification(AddNotificationRequest request);

    ResponseEntity<ResponseObject> updateStatusNotification(int notificationId);

    ResponseEntity<ResponseObject> deleteNotification(int notificationId);
}
