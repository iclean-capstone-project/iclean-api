package iclean.code.function.authentication.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.authen.*;
import iclean.code.function.authentication.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("api/v1/auth")
@Tag(name = "Authentication API")
@Validated
public class AuthController {
    @Autowired
    private AuthService authService;
    @PostMapping
    @Operation(summary = "Login into the system for admin and manager", description = "Return accessToken and user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login Successful"),
            @ApiResponse(responseCode = "401", description = "Wrong username or password"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> loginUsernamePassword(@RequestBody @Valid LoginUsernamePassword form) {
        return authService.loginUsingUsernameAndPassword(form);
    }
    @PostMapping("/refresh-token")
    @Operation(summary = "Login into the system for admin and manager", description = "Return accessToken and user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login Successful"),
            @ApiResponse(responseCode = "401", description = "Wrong username or password"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> refreshToken(@RequestBody @Valid TokenRefreshRequest form) {
        return authService.getNewAccessToken(form);
    }
    @PostMapping("/google")
    @Operation(summary = "Login into the system with Google", description = "Return accessToken and user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login Successful"),
            @ApiResponse(responseCode = "401", description = "Token ID is not valid"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required or not match pattern")
    })
    public ResponseEntity<ResponseObject> loginGoogle(@RequestBody @Valid TokenRequest token) {
        return authService.loginWithGoogle(token.getAccessToken());
    }
    @PostMapping("/facebook")
    @Operation(summary = "Login into the system with Facebook", description = "Return accessToken and user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login Successful"),
            @ApiResponse(responseCode = "401", description = "Token ID is not valid"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required or not match pattern")
    })
    public ResponseEntity<ResponseObject> loginFacebook(@RequestBody @Valid TokenRequest token) {
        return authService.loginWithFacebook(token.getAccessToken());
    }
    @PostMapping("/phone-number")
    @Operation(summary = "Login into the system with OTP", description = "Return accessToken and user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phone number are valid"),
            @ApiResponse(responseCode = "500", description = "Internal System Error"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required or not match pattern")
    })
    public ResponseEntity<ResponseObject> loginPhoneNumber(@RequestBody @Valid PhoneNumberDto phoneNumber) {
        return authService.loginUsingPhoneNumber(phoneNumber.getPhoneNumber());
    }
    @PostMapping("/otp-number")
    @Operation(summary = "Login into the system with OTP", description = "Return accessToken and user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login Successful"),
            @ApiResponse(responseCode = "401", description = "Invalid OTP"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required or not match pattern")
    })
    public ResponseEntity<ResponseObject> verifyPhoneNumber(@RequestBody @Valid LoginFormMobile formMobile) {
        return authService.loginUsingPhoneNumberAndOTP(formMobile);
    }
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Register the information after login", description = "Return status update successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Internal System Error - Contact the admin"),
            @ApiResponse(responseCode = "200", description = "Register Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Need access_token"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required or not match pattern")
    })
    public ResponseEntity<ResponseObject> updateInformation(@RequestPart(value = "fullName")
                                                            @NotNull(message = "Full name là trường bắt buộc")
                                                            @NotBlank(message = "Full name không được để trống")
                                                            String fullName,
                                                            @RequestPart(value = "dateOfBirth")
                                                            @Pattern(regexp = "^([0]?[1-9]|[1|2][0-9]|[3][0|1])[./-]([0]?[1-9]|[1][0-2])[./-]([0-9]{4}|[0-9]{2})$", message = "Invalid date Of Birth")
                                                            @NotNull(message = "Date Of Birth are required")
                                                            String dateOfBirth,
                                                            @RequestPart(value = "fileImage", required = false) MultipartFile file,
                                                            Authentication authentication) {
        return authService.updateInformationFirstLogin(JwtUtils.decodeToAccountId(authentication),
                new RegisterUserForm(fullName, dateOfBirth, file));
    }

    @PostMapping("/fcm-token")
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'admin', 'manager')")
    @Operation(summary = "Add fcm token for push notification", description = "Return status add new successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add new Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> addFcmToken(@RequestBody @Valid LogoutTokenDto dto,
                                                      Authentication authentication) {
        return authService.addFcmToken(dto, JwtUtils.decodeToAccountId(authentication));
    }

    @DeleteMapping("/logout")
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'admin', 'manager')")
    @Operation(summary = "Login into the system with OTP", description = "Return status delete successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete FCM Token Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> logout(@RequestBody @Valid LogoutTokenDto dto,
                                                 Authentication authentication) {
        return authService.logout(dto, JwtUtils.decodeToAccountId(authentication));
    }
}
