package iclean.code.data.dto.request;

import lombok.Data;

@Data
public class LoginFormMobile {
    private String phoneNumber;
    private String otpToken;
}
