package iclean.code.function.authentication.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.authen.LoginUsernamePassword;
import iclean.code.data.dto.request.authen.LoginFormMobile;
import iclean.code.data.dto.request.authen.RegisterUserForm;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<ResponseObject> loginUsingUsernameAndPassword(LoginUsernamePassword form);

    ResponseEntity<ResponseObject> loginWithGoogle(String googleToken);

    ResponseEntity<ResponseObject> loginWithFacebook(String facebookToken);

    ResponseEntity<ResponseObject> loginUsingPhoneNumber(String phoneNumber);

    ResponseEntity<ResponseObject> loginUsingPhoneNumberAndOTP(LoginFormMobile formMobile);

    ResponseEntity<ResponseObject> updateInformationFirstLogin(RegisterUserForm form);
}
