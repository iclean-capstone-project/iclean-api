package iclean.code.data.dto.request.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginFormMobile {

    @NotNull(message = "Số điện thoại là bắt buộc")
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9,}$", message = "Số điện thoại sai định dạng")
    @Schema(example = "0123456789")
    private String phoneNumber;

    @NotNull(message = "OTP là bắt buộc")
    @NotBlank(message = "OTP không được để trống")
    @Pattern(regexp = "^\\d{6}$", message = "OTP sai định dạng")
    @Schema(example = "012345")
    private String otpToken;
}
