package iclean.code.function.common.service.impl;

import iclean.code.data.dto.request.security.ValidateOTPRequest;
import iclean.code.function.common.service.QRCodeService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Random;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Override
    public String generateCodeValue() {
            return new DecimalFormat("000000")
                    .format(new Random().nextInt(999999));
    }

    @Override
    public String hashQrCode(String value) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(value);
    }

    @Override
    public boolean validateQRCode(ValidateOTPRequest validateOTPRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(validateOTPRequest.getUserOtpInput(),
                validateOTPRequest.getOtpToken());
    }
}
