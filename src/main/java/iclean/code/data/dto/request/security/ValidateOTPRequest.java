package iclean.code.data.dto.request.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateOTPRequest {
    private String userOtpInput;
    private String otpToken;
}
