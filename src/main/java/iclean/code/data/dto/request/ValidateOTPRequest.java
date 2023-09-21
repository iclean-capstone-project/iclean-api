package iclean.code.data.dto.request;

import lombok.Data;

@Data
public class ValidateOTPRequest {
    private String userOtpInput;
    private String otpToken;
}
