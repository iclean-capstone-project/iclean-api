package iclean.code.data.dto.request.jobapplication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobApplicationRequestDTO {

    @NotNull(message = "employee Id là bắt buộc")
    @NotBlank(message = "employee Id không được bỏ trống")
    @Schema(example = "1")
    private Integer registerEmployeeId;
}
