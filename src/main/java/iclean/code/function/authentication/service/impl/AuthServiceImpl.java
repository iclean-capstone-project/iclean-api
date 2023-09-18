package iclean.code.function.authentication.service.impl;

import iclean.code.config.JwtUtils;
import iclean.code.config.PhoneNumberOtpAuthenticationProvider;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.LoginForm;
import iclean.code.data.dto.request.LoginFormMobile;
import iclean.code.data.dto.response.JwtResponse;
import iclean.code.data.dto.response.UserPrinciple;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.UserNotFoundException;
import iclean.code.function.authentication.service.AuthService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Override
    public ResponseEntity<ResponseObject> login(LoginForm form) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new PreAuthenticatedAuthenticationToken(form.getUsername(), form.getPassword()));

            if (authentication != null) {
                UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
                String accessToken = jwtUtils.createAccessToken(userPrinciple);
                String refreshToken = jwtUtils.createRefreshToken(userPrinciple);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Login success!", new JwtResponse(accessToken, refreshToken)));
            } else ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(), "Wrong username or password.", null));

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(), "No username or password.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(), "No username or password.", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> loginWithIdToken(String token) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> loginPhoneNumber(String phoneNumber) {
        try {
            User user = userRepository.findUserByPhoneNumber(phoneNumber);
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            if (user == null) {
                //Call OTP Number API
                user = new User();
                String otpToken = "1234";
                user.setPhoneNumber(phoneNumber);
                user.setOtpToken(passwordEncoder.encode(otpToken));
                userRepository.save(user);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseObject(HttpStatus.CREATED.toString(),
                                "New Account Created",
                                null));
            }

            //Call OTP Number API
            String otpToken = "1234";
            user.setOtpToken(passwordEncoder.encode(otpToken));
            //Save OTP to Database
            userRepository.save(user);
            //

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new ResponseObject(HttpStatus.ACCEPTED.toString(),
                            "Founded Account",
                            null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> loginWithOtp(LoginFormMobile formMobile) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new PreAuthenticatedAuthenticationToken(formMobile.getPhoneNumber(),
                            formMobile.getOtpToken()));

            if (authentication != null) {
                UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
                String accessToken = jwtUtils.createAccessToken(userPrinciple);
                String refreshToken = jwtUtils.createRefreshToken(userPrinciple);
                return ResponseEntity.status(HttpStatus.ACCEPTED)
                        .body(new ResponseObject(HttpStatus.ACCEPTED.toString(),
                        "Login success!", new JwtResponse(accessToken, refreshToken)));
            } else ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                            "Wrong username or password.", null));


            User user = userRepository.findUserByPhoneNumber(formMobile.getPhoneNumber());
            if (user == null) throw new UserNotFoundException("Not Found User have Phone Number: "
                    + formMobile.getPhoneNumber());

            if (formMobile.getOtpToken().equals(user.getOtpToken())) {

                if (!ObjectUtils.anyNull(user.getFullName(), user.getRole())) {
                    user.setOtpToken(null);
                    userRepository.save(user);

                    //return accessToken, refresh Token, User Information
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseObject(HttpStatus.OK.toString(),
                                    "Login Successful",
                                    user));
                }

                user.setOtpToken(null);
                userRepository.save(user);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseObject(HttpStatus.CREATED.toString(),
                                "Need Update Information",
                                user));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Incorrect OTP Token",
                            null));

        } catch (Exception e) {
            if (e instanceof UserNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }
}
