package iclean.code.function.authentication.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.authen.*;
import iclean.code.data.dto.request.profile.UpdateProfileDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<ResponseObject> loginUsingUsernameAndPassword(LoginUsernamePassword form);

    ResponseEntity<ResponseObject> loginWithGoogle(String googleToken);

    ResponseEntity<ResponseObject> loginWithFacebook(String facebookToken);

    ResponseEntity<ResponseObject> loginUsingPhoneNumber(String phoneNumber);

    ResponseEntity<ResponseObject> loginUsingPhoneNumberAndOTP(LoginFormMobile formMobile);

    ResponseEntity<ResponseObject> updateInformationFirstLogin(Integer userId, RegisterUserForm form);

    ResponseEntity<ResponseObject> addFcmToken(LogoutTokenDto dto, Integer userId);

    ResponseEntity<ResponseObject> logout(LogoutTokenDto dto, Integer userId);

    ResponseEntity<ResponseObject> getNewAccessToken(TokenRefreshRequest form);

}
