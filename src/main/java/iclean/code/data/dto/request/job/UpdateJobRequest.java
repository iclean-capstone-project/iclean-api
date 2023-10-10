package iclean.code.data.dto.request.job;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UpdateJobRequest {

    @NotNull(message = "Chi tiết công việc không được để trống")
    @NotBlank(message = "Chi tiết công việc không được để rỗng")
    @Length(max = 200, message = "Tối đa 200 từ")
    private String description;

    @NotNull(message = "Ảnh công việc không được để trống")
    @Size(min = 0, max = 300, message = "Image size is large")
    private MultipartFile jobImageFile;

    @NotNull(message = "Tên công việc không được để trống")
    @NotBlank(message = "Tên công việc không được để rỗng")
    @Pattern(regexp = "^[A-Za-z-' ]+$",message = "Tên không hợp lệ")
    @Length(max = 200, message = "Tối đa 200 từ")
    private String jobName;
}
