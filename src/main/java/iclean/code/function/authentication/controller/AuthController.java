package iclean.code.function.authentication.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.authen.*;
import iclean.code.function.authentication.service.AuthService;
import iclean.code.service.impl.TwilioOTPServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/auth")
@Tag(name = "Authentication API")
public class AuthController {
    @Autowired
    private AuthService authService;
    @PostMapping("")
    @Operation(summary = "Login into the system for admin and manager", description = "Return accessToken and user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login Successful"),
            @ApiResponse(responseCode = "401", description = "Wrong username or password"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> loginUsernamePassword(@RequestBody @Valid LoginUsernamePassword form) {
        return authService.loginUsingUsernameAndPassword(form);
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
    @PostMapping("/update-information")
    @Operation(summary = "Update the information after login", description = "Return status update successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Internal System Error - Contact the admin"),
            @ApiResponse(responseCode = "200", description = "Update Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Need access_token"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required or not match pattern")
    })
    public ResponseEntity<ResponseObject> loginUsernamePassword(@RequestBody @Valid RegisterUserForm form) {
        return authService.updateInformationFirstLogin(form);
    }
}
