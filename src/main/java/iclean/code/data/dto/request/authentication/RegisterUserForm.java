package iclean.code.data.dto.request.authentication;
import iclean.code.data.enumjava.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserForm {

    @NotNull(message = "User ID là trường bắt buộc")
    @NotBlank(message = "User ID không được để trống")
    private Integer userId;

    @Pattern(regexp = "^[A-Za-z-' ]+$",message = "Tên không hợp lệ")
    @NotNull(message = "Full name là trường bắt buộc")
    @NotBlank(message = "Full name không được để trống")
    private String fullName;

    @NotNull(message = "Role là trường bắt buộc")
    @NotBlank(message = "Role không được để trống")
    private Role role;
}
