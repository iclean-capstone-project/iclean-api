package iclean.code.function.moneyrequest.controller;

import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.moneyrequest.CreateMoneyRequestRequest;
import iclean.code.data.dto.request.moneyrequest.ValidateMoneyRequest;
import iclean.code.data.dto.response.notification.GetNotificationResponse;
import iclean.code.function.moneyrequest.service.MoneyRequestService;
import iclean.code.utils.validator.ValidSortFields;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/money-request")
@Tag(name = "MoneyRequest API")
public class MoneyRequestController {
    @Autowired
    private MoneyRequestService moneyRequestService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    @Operation(summary = "Get all money requests of a user", description = "Return all money requests information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Money Requests Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getMoneyRequests(@RequestParam(name = "page", defaultValue = "1") int page,
                                                           @RequestParam(name = "size", defaultValue = "10") int size,
                                                           @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetNotificationResponse.class)
                                                               List<String> sortFields,
                                                           @RequestParam @Valid String phoneNumber) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);
        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
        }
        return moneyRequestService.getMoneyRequests(phoneNumber, pageable);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    @Operation(summary = "Get a money request of a user by id", description = "Return money request information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Money Request Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getMoneyRequest(@PathVariable Integer userId) {
        return moneyRequestService.getMoneyRequest(userId);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    @Operation(summary = "Resend OTP request of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new Money Request Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> resendOtpForMoneyRequest(String phoneNumber) {
        return moneyRequestService.resendOtp(phoneNumber);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    @Operation(summary = "Create new money request of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new Money Request Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createMoneyRequest(@RequestBody @Valid CreateMoneyRequestRequest request) {
        return moneyRequestService.createMoneyRequest(request);
    }

    @PostMapping("/validated")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    @Operation(summary = "Update a money request of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a Money Request Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> validateRequest(@RequestBody @Valid ValidateMoneyRequest request) {
        return moneyRequestService.validateMoneyRequest(request);
    }

}
