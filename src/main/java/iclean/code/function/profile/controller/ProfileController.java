package iclean.code.function.profile.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.profile.UpdateProfileDto;
import iclean.code.function.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("api/v1/profile")
@Tag(name = "Profile API")
@Validated
public class ProfileController {
    @Autowired
    private ProfileService profileService;
    @GetMapping
    @Operation(summary = "Get profile of a User", description = "Return user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile Information"),
            @ApiResponse(responseCode = "401", description = "Need Login into the system"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getProfile(Authentication authentication) {
        return profileService.getProfile(JwtUtils.decodeToAccountId(authentication));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update user the information", description = "Return status update successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "User not have permission to do this action"),
            @ApiResponse(responseCode = "200", description = "Update Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Need access_token"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required or not match pattern")
    })
    public ResponseEntity<ResponseObject> updateProfile(@RequestPart(value = "fullName")
                                                        @NotNull(message = "Full name là trường bắt buộc")
                                                        @NotBlank(message = "Full name không được để trống")
                                                        String fullName,
                                                        @RequestPart(value = "dateOfBirth")
                                                        @Pattern(regexp = "^([0]?[1-9]|[1|2][0-9]|[3][0|1])[./-]([0]?[1-9]|[1][0-2])[./-]([0-9]{4}|[0-9]{2})$", message = "Invalid date Of Birth")
                                                        @NotNull(message = "Date Of Birth are required")
                                                        String dateOfBirth,
                                                        @RequestPart(value = "fileImage", required = false) MultipartFile file,
                                                        Authentication authentication) {
        return profileService.updateProfile(JwtUtils.decodeToAccountId(authentication),
                new UpdateProfileDto(fullName, dateOfBirth, file));
    }
}
