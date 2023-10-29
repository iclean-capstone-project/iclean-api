package iclean.code.function.authentication.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import iclean.code.config.JwtUtils;
import iclean.code.data.domain.Address;
import iclean.code.data.domain.FcmToken;
import iclean.code.data.domain.RefreshToken;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.authen.*;
import iclean.code.data.dto.request.profile.UpdateProfileDto;
import iclean.code.data.dto.request.security.OtpAuthentication;
import iclean.code.data.dto.response.authen.JwtResponse;
import iclean.code.data.dto.response.authen.TokenRefreshResponse;
import iclean.code.data.dto.response.authen.UserInformationDto;
import iclean.code.data.dto.response.authen.UserPrinciple;
import iclean.code.data.enumjava.Role;
import iclean.code.data.repository.AddressRepository;
import iclean.code.data.repository.FcmTokenRepository;
import iclean.code.data.repository.RoleRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.authentication.service.AuthService;
import iclean.code.function.authentication.service.RefreshTokenService;
import iclean.code.service.StorageService;
import iclean.code.service.TwilioOTPService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Log4j2
public class AuthServiceImpl implements AuthService {

    @Value("${spring.mail.username}")
    private String companyEmail;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private FcmTokenRepository fcmTokenRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private TwilioOTPService twilioOTPService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RefreshTokenService refreshTokenService;
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
                String refreshToken = refreshTokenService.createRefreshToken(userPrinciple.getId()).getToken();
                User user = userRepository.findByUsername(form.getUsername());
                UserInformationDto userInformationDto = modelMapper.map(user, UserInformationDto.class);
                userInformationDto.setRoleName(user.getRole().getTitle());
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
                                String.format("Account has been disabled. Please contact %s for more information", companyEmail)
                                , null));
            }

            if (e instanceof AccountExpiredException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "The account has expired. Please contact "
                                        + "companyEmail" + " for more information", null));
            }

            if (e instanceof LockedException)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                String.format("Account has been locked. Please contact %s for more information", companyEmail),
                                null));

            if (e instanceof AuthenticationException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(),
                                "Wrong username or password.", null));
            }

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
                String refreshToken = refreshTokenService.createRefreshToken(userPrinciple.getId()).getToken();
                UserInformationDto userInformationDto = modelMapper.map(user, UserInformationDto.class);
                if (ObjectUtils.anyNull(user.getFullName(), user.getRole(), user.getDateOfBirth())) {
                    userInformationDto.setIsNewUser(true);

                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseObject(HttpStatus.OK.toString(),
                                    "Need Update Information!", new JwtResponse(accessToken, refreshToken, userInformationDto)));
                }
                userInformationDto.setRoleName(user.getRole().getTitle());
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
                String refreshToken = refreshTokenService.createRefreshToken(userPrinciple.getId()).getToken();
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
                String refreshToken = refreshTokenService.createRefreshToken(userPrinciple.getId()).getToken();
                UserInformationDto userInformationDto = modelMapper.map(user, UserInformationDto.class);

                if (!ObjectUtils.anyNull(user.getRole(), user.getDateOfBirth(), user.getFullName())) {
                    userRepository.save(user);

                    userInformationDto.setIsNewUser(true);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseObject(HttpStatus.OK.toString(),
                                    "Need Update Information",
                                    new JwtResponse(accessToken, refreshToken, userInformationDto)));
                }
                userInformationDto.setRoleName(user.getRole().getTitle());
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
                String refreshToken = refreshTokenService.createRefreshToken(userPrinciple.getId()).getToken();
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
                String refreshToken = refreshTokenService.createRefreshToken(userPrinciple.getId()).getToken();
                User user = userRepository.findUserByPhoneNumber(formMobile.getPhoneNumber());
                UserInformationDto userInformationDto = modelMapper.map(user, UserInformationDto.class);
                List<Address> addressList = addressRepository.findByUserIdAnAndIsDefault(user.getUserId());
                if (!addressList.isEmpty()) {
                    userInformationDto.setDefaultAddress(addressList.get(0).getDescription());
                }

                if (user.getRole() != null) {
                    userInformationDto.setRoleName(user.getRole().getTitle());
                }

                if (ObjectUtils.anyNull(user.getFullName(), user.getRole())) {
                    userInformationDto.setIsNewUser(true);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseObject(HttpStatus.OK.toString(), "Login success!",
                                    new JwtResponse(accessToken, refreshToken, userInformationDto)));
                }

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
    public ResponseEntity<ResponseObject> updateInformationFirstLogin(Integer userId, RegisterUserForm form) {
        try {
            User user = findUser(userId);
            if (Objects.nonNull(user.getRole())) {
                throw new UserNotHavePermissionException();
            }
            form.setFullName(Utils.convertToTitleCase(form.getFullName()));
            if (form.getRole().equalsIgnoreCase(Role.RENTER.name())) {
                modelMapper.map(form, user);
                user.setRoleId(roleRepository.findByTitle(Role.RENTER.name().toLowerCase()).getRoleId());
            } else if (form.getRole().equalsIgnoreCase(Role.EMPLOYEE.name())) {
                modelMapper.map(form, user);
                user.setRoleId(roleRepository.findByTitle(Role.EMPLOYEE.name().toLowerCase()).getRoleId());
            }
            user.setDateOfBirth(Utils.convertStringToLocalDateTime(form.getDateOfBirth()));
            if (Objects.nonNull(form.getFileImage())) {
                String avatar = storageService.uploadFile(form.getFileImage());
                user.setAvatar(avatar);
            }
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Information Successful", null));
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur.", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> addFcmToken(LogoutTokenDto dto, Integer userId) {
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
    public ResponseEntity<ResponseObject> logout(LogoutTokenDto dto, Integer userId) {
        try {
            FcmToken fcmToken = findFcmToken(dto.getFcmToken());
            if (!Objects.equals(fcmToken.getUser().getUserId(), userId))
                throw new UserNotHavePermissionException();
 
            refreshTokenService.deleteByRefreshToken(dto.getRefreshToken());
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

    @Override
    public ResponseEntity<ResponseObject> getNewAccessToken(TokenRefreshRequest request) {
        try {

            String requestRefreshToken = request.getRefreshToken();
            RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken);
            RefreshToken refreshTokenVerify = refreshTokenService.verifyExpiration(refreshToken);
            User user = findUser(refreshToken.getUserId());
            String accessToken = jwtUtils.createAccessToken(UserPrinciple.build(user));
            TokenRefreshResponse response = new TokenRefreshResponse(accessToken, refreshTokenVerify.getToken());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Access Token are created",
                            response));

        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
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
