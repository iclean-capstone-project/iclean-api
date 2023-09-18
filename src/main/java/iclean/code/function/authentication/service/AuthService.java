package iclean.code.function.authentication.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.LoginForm;
import iclean.code.data.dto.request.LoginFormMobile;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<ResponseObject> login(LoginForm form);

    ResponseEntity<ResponseObject> loginWithIdToken(String token);

    ResponseEntity<ResponseObject> loginPhoneNumber(String phoneNumber);

    ResponseEntity<ResponseObject> loginWithOtp(LoginFormMobile formMobile);
}
