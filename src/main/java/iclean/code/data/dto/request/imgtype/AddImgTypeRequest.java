package iclean.code.data.dto.request.imgtype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddImgTypeRequest {
    @NotNull(message = "Loại hình ảnh không được trống")
    @NotBlank(message = "Loại hình ảnh không được trống")
    @Length(max = 200, message = "Tối đa 200 từ")
    private String titleImgType;
}
