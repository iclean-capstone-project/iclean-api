package iclean.code.function.notification.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.notification.AddNotificationRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    ResponseEntity<ResponseObject> addNotification(AddNotificationRequest request);

    ResponseEntity<ResponseObject> getNotificationById(Integer notificationId, Integer userId);

    ResponseEntity<ResponseObject> getNotifications(Integer userIdAuth, Pageable pageable);

    ResponseEntity<ResponseObject> updateStatusNotification(int notificationId, int userId);

    ResponseEntity<ResponseObject> deleteNotification(int notificationId, int userId);

    ResponseEntity<ResponseObject> readAllNotification(int userId);
}
