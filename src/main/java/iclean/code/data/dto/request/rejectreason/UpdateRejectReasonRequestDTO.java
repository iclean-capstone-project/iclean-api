package iclean.code.data.dto.request.rejectreason;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateRejectReasonRequestDTO {
    @NotNull(message = "Nội dung là bắt buộc")
    @NotBlank(message = "Nội dung không để trống")
    @Schema(example = "Địa chỉ không đúng với mô tả của khách hàng")
    @NotBlank(message = "Nội dung không để trống")
    private String rj_content;
}
