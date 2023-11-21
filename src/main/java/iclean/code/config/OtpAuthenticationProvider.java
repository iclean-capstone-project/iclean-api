package iclean.code.config;

import iclean.code.data.domain.User;
import iclean.code.data.dto.request.security.ValidateOTPRequest;
import iclean.code.data.dto.request.security.OtpAuthentication;
import iclean.code.data.dto.response.authen.UserPrinciple;
import iclean.code.data.repository.UserRepository;
import iclean.code.service.TwilioOTPService;
import iclean.code.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class OtpAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TwilioOTPService twilioOTPService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String phoneNumber = authentication.getName();
        String otp = authentication.getCredentials().toString();
        ValidateOTPRequest validateOTPRequest = new ValidateOTPRequest();
        User user = userRepository.findUserByPhoneNumber(phoneNumber);
        validateOTPRequest.setUserOtpInput(otp);
        validateOTPRequest.setOtpToken(user.getOtpToken());

        if (twilioOTPService.validateOTP(validateOTPRequest) && user.getExpiredToken().isAfter(Utils.getLocalDateTimeNow())) {
            user.setOtpToken(null);
            userRepository.save(user);
            User principal = userRepository.findUserByPhoneNumber(phoneNumber);
            return new OtpAuthentication(UserPrinciple.build(principal), otp);

        } else if (twilioOTPService.validateOTP(validateOTPRequest) && !user.getExpiredToken().isAfter(Utils.getLocalDateTimeNow())) {
            throw new BadCredentialsException("The OTP had expired!");
        }
        throw new BadCredentialsException("Wrong OTP!");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OtpAuthentication.class.equals(authentication);
    }
}
