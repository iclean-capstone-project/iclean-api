package iclean.code.function.authentication.service.impl;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import iclean.code.config.TwilioConfig;
import iclean.code.data.dto.request.ValidateOTPRequest;
import iclean.code.data.repository.SystemParameterRepository;
import iclean.code.function.authentication.service.TwilioOTPService;
import iclean.code.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.DecimalFormat;
import java.util.Random;

@Service
public class TwilioOTPServiceImpl implements TwilioOTPService {
    @Autowired
    private TwilioConfig twilioConfig;

    @Autowired
    private SystemParameterRepository systemParameterRepository;

    private String getMessageDefault() {
        try {

            return systemParameterRepository
                    .findSystemParameterByParameterField("otp_message_default")
                    .getParameterValue();

        } catch (EntityNotFoundException e) {
            return twilioConfig.getDefaultMessage();
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    private String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }

    @Override
    public String sendAndGetOTPToken(String phoneNumberDto) {
        try {
            String pattern = "^0";
            phoneNumberDto = phoneNumberDto.replaceFirst(pattern, "+84");
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            PhoneNumber to = new PhoneNumber(phoneNumberDto);
            PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber());
            String otp = generateOTP();
            String defaultMessage = getMessageDefault();
            if (Utils.isNullOrEmpty(defaultMessage) || !defaultMessage.contains("%s"))
                defaultMessage += "%s";

            String otpMessage = String.format(defaultMessage, otp);
            Message message = Message
                    .creator(to,
                            from,
                            otpMessage)
                    .create();
        return passwordEncoder.encode(otp);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean validateOTP(ValidateOTPRequest validateOTPRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(validateOTPRequest.getUserOtpInput(),
                validateOTPRequest.getOtpToken());
    }
}
