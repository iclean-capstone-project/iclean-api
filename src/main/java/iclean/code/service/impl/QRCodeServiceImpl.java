package iclean.code.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import iclean.code.data.dto.request.security.ValidateOTPRequest;
import iclean.code.service.QRCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Random;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Value("${iclean.app.qrcode.width}")
    private Integer width;
    @Value("${iclean.app.qrcode.height}")
    private Integer height;

    @Override
    public BitMatrix generateQRCodeImage(String value) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            return qrCodeWriter.encode(value, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
