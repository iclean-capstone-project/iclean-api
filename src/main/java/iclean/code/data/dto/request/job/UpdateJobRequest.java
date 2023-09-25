package iclean.code.data.dto.request.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJobRequest {

    @NotNull(message = "Chi tiết công việc không được để trống")
    @NotBlank(message = "Chi tiết công việc không được để rỗng")
    @Length(max = 200, message = "Tối đa 200 từ")
    private String description;

    @NotNull(message = "Ảnh công việc không được để trống")
    @NotBlank(message = "Ảnh công việc không được để rỗng")
    @Length(max = 200, message = "Tối đa 200 từ")
    private String jobImage;

    @NotNull(message = "Tên công việc không được để trống")
    @NotBlank(message = "Tên công việc không được để rỗng")
    @Pattern(regexp = "^[A-Za-z-' ]+$",message = "Tên không hợp lệ")
    @Length(max = 200, message = "Tối đa 200 từ")
    private String jobName;
}
