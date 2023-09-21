package iclean.code.data.dto.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class PhoneNumberDto {

    @NotNull(message = "Số điện thoại là bắt buộc")
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9,}$", message = "Số điện thoại sai định dạng")
    private String phoneNumber;
}
