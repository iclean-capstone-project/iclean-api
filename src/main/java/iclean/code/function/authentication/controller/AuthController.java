package iclean.code.function.authentication.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.LoginForm;
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
}
