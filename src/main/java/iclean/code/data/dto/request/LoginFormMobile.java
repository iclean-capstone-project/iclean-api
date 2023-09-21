package iclean.code.data.dto.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class LoginFormMobile {

    @NotNull(message = "Số điện thoại là bắt buộc")
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9,}$", message = "Số điện thoại sai định dạng")
    private String phoneNumber;

    @NotNull(message = "OTP là bắt buộc")
    @NotBlank(message = "OTP không được để trống")
    @Pattern(regexp = "^\\d{6}$", message = "OTP sai định dạng")
    private String otpToken;
}
