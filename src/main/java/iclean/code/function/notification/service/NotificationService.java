package iclean.code.function.notification.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.notification.AddNotificationRequest;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    ResponseEntity<ResponseObject> getAllNotification();

    ResponseEntity<ResponseObject> getNotificationById(int notificationId);

    ResponseEntity<ResponseObject> getNotificationByUserId(int userId);

    ResponseEntity<ResponseObject> addNotification(AddNotificationRequest request);

    ResponseEntity<ResponseObject> updateStatusNotification(int notificationId);

    ResponseEntity<ResponseObject> deleteNotification(int notificationId);
}
