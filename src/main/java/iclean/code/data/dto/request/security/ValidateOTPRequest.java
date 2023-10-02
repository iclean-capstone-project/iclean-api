package iclean.code.data.dto.request.security;

import lombok.Data;

@Data
public class ValidateOTPRequest {
    private String userOtpInput;
    private String otpToken;
}
