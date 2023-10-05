package iclean.code.function.authentication.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import iclean.code.config.JwtUtils;
import iclean.code.data.domain.Address;
import iclean.code.data.domain.FcmToken;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.authen.*;
import iclean.code.data.dto.request.security.OtpAuthentication;
import iclean.code.data.dto.response.authen.JwtResponse;
import iclean.code.data.dto.response.authen.UserInformationDto;
import iclean.code.data.dto.response.authen.UserPrinciple;
import iclean.code.data.enumjava.Role;
import iclean.code.data.repository.FcmTokenRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.authentication.service.AuthService;
import iclean.code.service.TwilioOTPService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Log4j2
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FcmTokenRepository fcmTokenRepository;
    @Autowired
    private TwilioOTPService twilioOTPService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Override
    public ResponseEntity<ResponseObject> loginUsingUsernameAndPassword(LoginUsernamePassword form) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword()));

            if (authentication != null) {
                UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
                String accessToken = jwtUtils.createAccessToken(userPrinciple);
                String refreshToken = jwtUtils.createRefreshToken(userPrinciple);
                User user = userRepository.findByUsername(form.getUsername());
                UserInformationDto userInformationDto = modelMapper.map(user, UserInformationDto.class);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(), "Login success!",
                                new JwtResponse(accessToken, refreshToken, userInformationDto)));

            } else ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                            "Wrong username or password.", null));

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                            "No username or password.", null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof DisabledException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "Account has been locked. Please contact " +
                                        "companyEmail" + " for more information", null));
            } else if (e instanceof AccountExpiredException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "The account has expired. Please contact "
                                        + "companyEmail" + " for more information", null));
            } else if (e instanceof AuthenticationException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "Wrong username or password.", null));
            } else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "Account is not NULL.", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> loginWithGoogle(String googleToken) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TokenRequest tokenRequest = mapper.readValue(googleToken, TokenRequest.class);
            String tokenValue = tokenRequest.getAccessToken();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(tokenValue);
            String email = decodedToken.getEmail();
            User user = userRepository.findByEmail(email);
            if (user != null) {
                UserPrinciple userPrinciple = UserPrinciple.build(user);
                String accessToken = jwtUtils.createAccessToken(userPrinciple);
                String refreshToken = jwtUtils.createRefreshToken(userPrinciple);
                UserInformationDto userInformationDto = modelMapper.map(user, UserInformationDto.class);
                if (ObjectUtils.anyNull(user.getFullName(), user.getRole())) {
                    userInformationDto.setIsNewUser(true);

                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseObject(HttpStatus.OK.toString(),
                                    "Need Update Information!", new JwtResponse(accessToken, refreshToken, userInformationDto)));
                }
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(),
                                "Login successful!", new JwtResponse(accessToken, refreshToken, userInformationDto)));
            } else {
                user = new User();
                user.setEmail(email);
                user.setFullName(decodedToken.getName());

                userRepository.save(user);
                UserPrinciple userPrinciple = UserPrinciple.build(user);
                String accessToken = jwtUtils.createAccessToken(userPrinciple);
                String refreshToken = jwtUtils.createRefreshToken(userPrinciple);
                UserInformationDto userInformationDto = modelMapper.map(user, UserInformationDto.class);

                userInformationDto.setIsNewUser(true);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(),
                                "Need Update Information!", new JwtResponse(accessToken, refreshToken, userInformationDto)));

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "Access Token is not valid.", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> loginWithFacebook(String facebookToken) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TokenRequest tokenRequest = mapper.readValue(facebookToken, TokenRequest.class);
            String tokenValue = tokenRequest.getAccessToken();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(tokenValue);
            String facebookUid = decodedToken.getUid();
            User user = userRepository.findByFacebookUid(facebookUid);
            if (user != null) {
                UserPrinciple userPrinciple = UserPrinciple.build(user);
                String accessToken = jwtUtils.createAccessToken(userPrinciple);
                String refreshToken = jwtUtils.createRefreshToken(userPrinciple);
                UserInformationDto userInformationDto = modelMapper.map(user, UserInformationDto.class);

                if (!ObjectUtils.anyNull(userInformationDto.getFullName())) {
                    userRepository.save(user);

                    userInformationDto.setIsNewUser(true);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseObject(HttpStatus.OK.toString(),
                                    "Need Update Information",
                                    new JwtResponse(accessToken, refreshToken, userInformationDto)));
                }

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(),
                                "Login Successful",
                                new JwtResponse(accessToken, refreshToken, userInformationDto)));

            } else {
                user = new User();
                user.setFacebookUid(facebookUid);
                user.setFullName(decodedToken.getName());

                userRepository.save(user);
                UserPrinciple userPrinciple = UserPrinciple.build(user);
                String accessToken = jwtUtils.createAccessToken(userPrinciple);
                String refreshToken = jwtUtils.createRefreshToken(userPrinciple);
                UserInformationDto userInformationDto = modelMapper.map(user, UserInformationDto.class);
                userInformationDto.setIsNewUser(true);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(),
                                "Need Update Information!", new JwtResponse(accessToken, refreshToken, userInformationDto)));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof DisabledException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "Account has been locked. Please contact " + "companyEmail" + " for more information", null));
            } else if (e instanceof AccountExpiredException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "The account has expired. Please contact " + "companyEmail" + " for more information", null));
            } else if (e instanceof AuthenticationException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "Wrong username or password.", null));
            } else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "Account is not NULL.", null));
        }

    }

    @Override
    public ResponseEntity<ResponseObject> loginUsingPhoneNumber(String phoneNumber) {
        try {
            User user = userRepository.findUserByPhoneNumber(phoneNumber);

            if (user == null) {
                user = new User();
                String otpHashToken = twilioOTPService.sendAndGetOTPToken(phoneNumber);
                user.setPhoneNumber(phoneNumber);
                user.setOtpToken(otpHashToken);
                userRepository.save(user);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(),
                                "New Account Created",
                                null));
            }
            String otpHashToken = twilioOTPService.sendAndGetOTPToken(phoneNumber);
            user.setOtpToken(otpHashToken);
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Founded Account",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> loginUsingPhoneNumberAndOTP(LoginFormMobile formMobile) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new OtpAuthentication(formMobile.getPhoneNumber(), formMobile.getOtpToken()));

            if (authentication != null) {
                UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
                String accessToken = jwtUtils.createAccessToken(userPrinciple);
                String refreshToken = jwtUtils.createRefreshToken(userPrinciple);
                User user = userRepository.findUserByPhoneNumber(formMobile.getPhoneNumber());
                UserInformationDto userInformationDto = modelMapper.map(user, UserInformationDto.class);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(), "Login success!",
                                new JwtResponse(accessToken, refreshToken, userInformationDto)));

            } else ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                            "Wrong username or password.", null));

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                            "No username or password.", null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof DisabledException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "Account has been locked. Please contact " +
                                        "companyEmail" + " for more information", null));
            } else if (e instanceof AccountExpiredException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "The account has expired. Please contact "
                                        + "companyEmail" + " for more information", null));
            } else if (e instanceof AuthenticationException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "Wrong OTP.", null));
            } else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "Account is not NULL.", null));
        }
    }
    private FcmToken findFcmToken(String fcmToken) {
        return fcmTokenRepository.findByToken(fcmToken).orElseThrow(() -> new NotFoundException("FcmToken not found"));
    }

    private User findUser(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }
    @Override
    public ResponseEntity<ResponseObject> updateInformationFirstLogin(RegisterUserForm form) {
            try {
                User user = findUser(form.getUserId());

                if (form.getRole().equals(Role.RENTER)) {
                    user = modelMapper.map(form, User.class);

                } else if (form.getRole().equals(Role.EMPLOYEE)) {
                    user = modelMapper.map(form, User.class);

                }
                userRepository.save(user);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject(HttpStatus.OK.toString(),
                                "Update Information Successful", null));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                "Something wrong occur.", null));
            }
    }

    @Override
    public ResponseEntity<ResponseObject> addFcmToken(FcmTokenDto dto, Integer userId) {
        try {
            FcmToken fcmToken = modelMapper.map(dto, FcmToken.class);
            User user = findUser(userId);
            fcmToken.setUser(user);
            fcmTokenRepository.save(fcmToken);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create new Fcm Token Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteFcmToken(FcmTokenDto dto, Integer userId) {
        try {
            FcmToken fcmToken = findFcmToken(dto.getFcmToken());
            if (!Objects.equals(fcmToken.getUser().getUserId(), userId))
                throw new UserNotHavePermissionException();

            fcmTokenRepository.delete(fcmToken);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete FCM Token Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof UserNotHavePermissionException)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }
}
