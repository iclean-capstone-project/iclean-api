package iclean.code.function.helperregistration.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.helperinformation.AcceptHelperRequest;
import iclean.code.data.dto.request.helperinformation.ConfirmHelperRequest;
import iclean.code.data.dto.request.helperinformation.HelperRegistrationRequest;
import iclean.code.data.dto.request.helperinformation.CancelHelperRequest;
import iclean.code.data.dto.response.helperinformation.GetHelperInformationRequestResponse;
import iclean.code.function.helperregistration.service.HelperRegistrationService;
import iclean.code.utils.validator.ValidSortFields;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/helper-registration")
@Tag(name = "Helper Registration API")
public class HelperRegistrationController {
    @Autowired
    private HelperRegistrationService helperRegistrationService;

    @GetMapping
    @Operation(summary = "Get all new employees of a user", description = "Return all new employees information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New Employees Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('manager')")
    public ResponseEntity<ResponseObject> getAllRequestToBecomeHelper(@RequestParam(defaultValue = "true") Boolean isAllRequest,
                                                                      @RequestParam(name = "page", defaultValue = "1") int page,
                                                                      @RequestParam(name = "size", defaultValue = "10") int size,
                                                                      @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetHelperInformationRequestResponse.class) List<String> sortFields,
                                                                      Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);

        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields, GetHelperInformationRequestResponse.class);
        }
        return helperRegistrationService.getAllRequestToBecomeHelper(JwtUtils.decodeToAccountId(authentication), isAllRequest, pageable);
    }

    @GetMapping("all")
    @Operation(summary = "Get all helper information", description = "Return all helper information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Helpers information Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('manager')")
    public ResponseEntity<ResponseObject> getHelpersInformation(@RequestParam(defaultValue = "true") Boolean isAllRequest,
                                                                @RequestParam(name = "page", defaultValue = "1") int page,
                                                                @RequestParam(name = "size", defaultValue = "10") int size,
                                                                @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetHelperInformationRequestResponse.class) List<String> sortFields,
                                                                Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);

        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
        }
        return helperRegistrationService.getHelpersInformation(JwtUtils.decodeToAccountId(authentication), isAllRequest, pageable);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get all helper information", description = "Return all helper information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Helpers information Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('manager')")
    public ResponseEntity<ResponseObject> getHelperInformationById(@PathVariable Integer id) {
        return helperRegistrationService.getHelperInformation(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('renter')")
    @Operation(summary = "Create new registration helper to become a helper of app", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new registration helper successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createHelperRequest(@RequestPart("email") String email,
                                                              @RequestPart("frontIdCard") MultipartFile frontIdCard,
                                                              @RequestPart("backIdCard") MultipartFile backIdCard,
                                                              @RequestPart("avatar") MultipartFile avatar,
                                                              @RequestPart(value = "others", required = false) List<MultipartFile> others,
                                                              @RequestPart(value = "service") List<Integer> services,
                                                              Authentication authentication) {
        return helperRegistrationService.createHelperRegistration(
                new HelperRegistrationRequest(email, frontIdCard, backIdCard, avatar, others, services),
                JwtUtils.decodeToAccountId(authentication));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update a registration helper of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a registration helper successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('employee')")
    public ResponseEntity<ResponseObject> addMoreService(@RequestPart(value = "others", required = false) List<MultipartFile> others,
                                                         @RequestPart(value = "service", required = false) List<Integer> services,
                                                         Authentication authentication) {
        return helperRegistrationService.updateMoreServiceForHelper(JwtUtils.decodeToAccountId(authentication), others, services);
    }

    @PutMapping("/cancellation/{id}")
    @PreAuthorize("hasAuthority('manager')")
    @Operation(summary = "Cancel a request become to a helper by helper information id", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete a RegisterEmployee Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> cancelHelperRequest(Authentication authentication, @PathVariable Integer id, @RequestBody CancelHelperRequest request) {
        return helperRegistrationService.cancelHelperInformationRequest(JwtUtils.decodeToAccountId(authentication), id, request);
    }

    @PostMapping("/acceptance/{id}")
    @PreAuthorize("hasAuthority('manager')")
    @Operation(summary = "Cancel a request become to a helper by helper information id", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete a RegisterEmployee Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> acceptHelperRequest(Authentication authentication, @PathVariable Integer id) {
        return helperRegistrationService.acceptHelperInformation(JwtUtils.decodeToAccountId(authentication), id);
    }

    @PostMapping("/confirmation/{id}")
    @PreAuthorize("hasAuthority('manager')")
    @Operation(summary = "Cancel a request become to a helper by helper information id", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete a RegisterEmployee Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> confirmHelperRequest(Authentication authentication, @PathVariable Integer id,
                                                               @RequestBody ConfirmHelperRequest request) {
        return helperRegistrationService.confirmHelperInformation(JwtUtils.decodeToAccountId(authentication), id, request);
    }
}
