package iclean.code.function.authentication.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.*;
import iclean.code.function.authentication.service.AuthService;
import iclean.code.function.authentication.service.impl.TwilioOTPServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    TwilioOTPServiceImpl twilioOTPServiceImpl;
    @PostMapping("")
    public ResponseEntity<ResponseObject> loginUsernamePassword(@RequestBody @Valid LoginUsernamePassword form) {
        return authService.loginUsingUsernameAndPassword(form);
    }
    @PostMapping("/google")
    public ResponseEntity<ResponseObject> loginGoogle(@RequestBody @Valid TokenRequest token) {
        return authService.loginWithGoogle(token.getAccessToken());
    }
    @PostMapping("/facebook")
    public ResponseEntity<ResponseObject> loginFacebook(@RequestBody @Valid TokenRequest token) {
        return authService.loginWithGoogle(token.getAccessToken());
    }
    @PostMapping("/phone-number")
    public ResponseEntity<ResponseObject> loginPhoneNumber(@RequestBody @Valid PhoneNumberDto phoneNumber) {
        return authService.loginUsingPhoneNumber(phoneNumber.getPhoneNumber());
    }
    @PostMapping("/otp-number")
    public ResponseEntity<ResponseObject> verifyPhoneNumber(@RequestBody @Valid LoginFormMobile formMobile) {
        return authService.loginUsingPhoneNumberAndOTP(formMobile);
    }
    @PostMapping("/update-information")
    public ResponseEntity<ResponseObject> loginUsernamePassword(@RequestBody @Valid RegisterUserForm form) {
        return authService.updateInformationFirstLogin(form);
    }
}
