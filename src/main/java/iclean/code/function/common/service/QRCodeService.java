package iclean.code.function.common.service;

import iclean.code.data.dto.request.security.ValidateOTPRequest;

public interface QRCodeService {
    public String generateCodeValue();
    public String hashQrCode(String value);
    public boolean validateQRCode(ValidateOTPRequest validateOTPRequest);
}