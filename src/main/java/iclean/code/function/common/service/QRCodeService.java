package iclean.code.function.common.service;

import com.google.zxing.common.BitMatrix;
import iclean.code.data.dto.request.security.ValidateOTPRequest;

public interface QRCodeService {
    public BitMatrix generateQRCodeImage(String value);
    public String generateCodeValue();
    public String hashQrCode(String value);
    public boolean validateQRCode(ValidateOTPRequest validateOTPRequest);
}