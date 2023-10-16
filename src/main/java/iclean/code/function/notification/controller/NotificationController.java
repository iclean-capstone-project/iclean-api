package iclean.code.function.notification.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.notification.GetNotificationDTO;
import iclean.code.data.dto.request.notification.AddNotificationRequest;
import iclean.code.data.dto.response.address.GetAddressResponseDto;
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

    @GetMapping
    @Operation(summary = "Get All Notification", description = "Return All Notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get All Notification success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getAllNotification(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetNotificationDTO.class) List<String> sortFields,
            Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);

        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
        }
        return notificationService.getAllNotification(JwtUtils.decodeToAccountId(authentication), pageable);
    }

    @GetMapping(value = "{notificationId}")
    @Operation(summary = "Get Notification By Id", description = "Return Notification By Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Notification By Id success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getNotificationById(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetNotificationDTO.class) List<String> sortFields,
            @PathVariable("notificationId") @Valid int notificationId,
            Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);

        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
        }
        return notificationService.getNotificationById(notificationId, JwtUtils.decodeToAccountId(authentication), pageable);
    }

    @GetMapping(value = "/user/{userId}")
    @Operation(summary = "Get Notification By User Id", description = "Return Notification By User Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Notification By User Id success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getNotificationByUserId(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetNotificationDTO.class) List<String> sortFields,
            Authentication authentication,
            @PathVariable("userId") @Valid Integer userId) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);
        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
        }
        return notificationService.getNotificationByUserId(userId, JwtUtils.decodeToAccountId(authentication), pageable);
    }

    @PostMapping
    @Operation(summary = "Add Notification", description = "Return fail or success message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add Notification"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> addNotification(@RequestBody @Valid AddNotificationRequest request) {
        return notificationService.addNotification(request);
    }

    @PutMapping(value = "{notificationId}")
    @Operation(summary = "Add Notification", description = "Return fail or success message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add Notification"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> updateStatusNotification(@PathVariable("notificationId") int notificationId) {
        return notificationService.updateStatusNotification(notificationId);
    }

    @DeleteMapping(value = "{notificationId}")
    public ResponseEntity<ResponseObject> deleteNotification(@PathVariable("notificationId") @Valid int notificationId) {
        return notificationService.deleteNotification(notificationId);
    }
}
