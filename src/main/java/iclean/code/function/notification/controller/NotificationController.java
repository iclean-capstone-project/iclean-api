package iclean.code.function.notification.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.notification.GetNotificationDTO;
import iclean.code.function.notification.service.NotificationService;
import iclean.code.utils.validator.ValidSortFields;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/notification")
@Tag(name = "Notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping(value = "{notificationId}")
    @Operation(summary = "Get Notification By Id", description = "Return Notification By Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Notification By Id success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'admin', 'manager')")
    public ResponseEntity<ResponseObject> getNotificationById(
            @PathVariable("notificationId") @Valid int notificationId,
            Authentication authentication) {
        return notificationService.getNotificationById(notificationId, JwtUtils.decodeToAccountId(authentication));
    }

    @GetMapping
    @Operation(summary = "Get Notification", description = "Return Notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Notification By User Id success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'admin', 'manager')")
    public ResponseEntity<ResponseObject> getNotifications(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetNotificationDTO.class) List<String> sortFields,
            Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);
        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
        }
        return notificationService.getNotifications(JwtUtils.decodeToAccountId(authentication), pageable);
    }

    @PutMapping(value = "{notificationId}")
    @Operation(summary = "Read a Notification", description = "Return fail or success message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add Notification"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'admin', 'manager')")
    public ResponseEntity<ResponseObject> updateStatusNotification(@PathVariable("notificationId") int notificationId,
                                                                   Authentication authentication) {
        return notificationService.updateStatusNotification(notificationId, JwtUtils.decodeToAccountId(authentication));
    }

    @PutMapping
    @Operation(summary = "Read All Notification", description = "Return fail or success message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add Notification"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'admin', 'manager')")
    public ResponseEntity<ResponseObject> updateStatusNotification(Authentication authentication) {
        return notificationService.readAllNotification(JwtUtils.decodeToAccountId(authentication));
    }

    @DeleteMapping(value = "{notificationId}")
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'admin', 'manager')")
    public ResponseEntity<ResponseObject> deleteNotification(@PathVariable("notificationId") @Valid int notificationId,
                                                             Authentication authentication) {
        return notificationService.deleteNotification(notificationId, JwtUtils.decodeToAccountId(authentication));
    }
}
