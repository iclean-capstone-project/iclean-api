package iclean.code.service;

import iclean.code.data.dto.request.ValidateOTPRequest;

public interface TwilioOTPService {
    public String sendAndGetOTPToken(String phoneNumberDto);
    public boolean validateOTP(ValidateOTPRequest validateOTPRequest);
}
