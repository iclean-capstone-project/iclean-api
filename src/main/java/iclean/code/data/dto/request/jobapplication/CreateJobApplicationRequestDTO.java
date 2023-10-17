package iclean.code.data.dto.request.jobapplication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateJobApplicationRequestDTO {

    @NotNull(message = "employee Id là bắt buộc")
    @NotBlank(message = "employee Id không được bỏ trống")
    @Schema(example = "Nhật Linh")
    private String fullName;

    @NotNull(message = "employee Id là bắt buộc")
    @NotBlank(message = "employee Id không được bỏ trống")
    @Schema(example = "Nhật Linh")
    private String phoneNumber;
}
