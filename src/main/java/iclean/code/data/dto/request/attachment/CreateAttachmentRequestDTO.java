package iclean.code.data.dto.request.attachment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class CreateAttachmentRequestDTO {

    @NotNull(message = "employee Id là bắt buộc")
    @NotBlank(message = "employee Id không được bỏ trống")
    @Schema(example = "Nhật Linh")
    private String fullName;
}
