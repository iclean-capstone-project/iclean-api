package iclean.code.function.notification.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.notification.AddNotificationRequest;
import iclean.code.function.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ResponseObject> getAllNotification() {
        return notificationService.getAllNotification();
    }

    @GetMapping(value = "{notificationId}")
    public ResponseEntity<ResponseObject> getNotificationById(@PathVariable("notificationId") @Valid int notificationId) {
        return notificationService.getNotificationById(notificationId);
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<ResponseObject> getNotificationByUserId(@PathVariable("userId") @Valid int userId) {
        return notificationService.getNotificationByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<ResponseObject> addNotification(@RequestBody @Valid AddNotificationRequest request) {
        return notificationService.addNotification(request);
    }

    @PutMapping(value = "{notificationId}")
    public ResponseEntity<ResponseObject> updateStatusNotification(@PathVariable("notificationId") int notificationId) {
        return notificationService.updateStatusNotification(notificationId);
    }

    @DeleteMapping(value = "{notificationId}")
    public ResponseEntity<ResponseObject> deleteNotification(@PathVariable("notificationId") @Valid int notificationId) {
        return notificationService.deleteNotification(notificationId);
    }
}
