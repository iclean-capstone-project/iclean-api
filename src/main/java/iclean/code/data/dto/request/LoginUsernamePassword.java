package iclean.code.data.dto.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginUsernamePassword {
    @NotNull(message = "Tên đăng nhập là bắt buộc")
    @NotBlank(message = "Tên đăng nhập không được bỏ trống")
    private String username;

    @NotNull(message = "Mật khẩu bắt buộc")
    @NotBlank(message = "Mật khẩu không được bỏ trống")
    private String password;

}
