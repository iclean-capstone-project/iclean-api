package iclean.code.data.dto.request.authen;
import iclean.code.data.enumjava.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserForm {
    
    @Pattern(regexp = "^[A-Za-z-' ]+$",message = "Tên không hợp lệ")
    @NotNull(message = "Full name là trường bắt buộc")
    @NotBlank(message = "Full name không được để trống")
    private String fullName;

    @NotNull(message = "Role là trường bắt buộc")
    @NotBlank(message = "Role không được để trống")
    private Role role;

    private String dateOfBirth;

    private String gender;

    private MultipartFile file;
}
