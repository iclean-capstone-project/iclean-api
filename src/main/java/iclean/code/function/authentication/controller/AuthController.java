package iclean.code.function.authentication.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.LoginForm;
import iclean.code.data.dto.request.LoginFormMobile;
import iclean.code.data.dto.request.PhoneNumberForm;
import iclean.code.function.authentication.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("")
    public ResponseEntity<ResponseObject> login(@RequestBody LoginForm form) {
        return authService.login(form);
    }
    @PostMapping("/token-firebase")
    public ResponseEntity<ResponseObject> login(@RequestBody String token) {
        return authService.loginWithIdToken(token);
    }
    @PostMapping("/phone-number")
    public ResponseEntity<ResponseObject> loginPhoneNumber(@RequestBody PhoneNumberForm phoneNumber) {
        return authService.loginPhoneNumber(phoneNumber.getPhoneNumber());
    }
    @PostMapping("/otp-number")
    public ResponseEntity<ResponseObject> loginPhoneNumber(@RequestBody LoginFormMobile formMobile) {
        return authService.loginWithOtp(formMobile);
    }
}
