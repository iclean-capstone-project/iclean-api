package iclean.code.data.dto.request.job;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class CreateJobRequest {

    @NotNull(message = "Description cannot be null")
    @NotBlank(message = "Description is empty")
    @Length(max = 200, message = "Tối đa 200 từ")
    private String description;

    @NotNull(message = "Job name cannot be null")
    @NotBlank(message = "Job name is empty")
    @Pattern(regexp = "^[A-Za-z-' ]+$",message = "Tên không hợp lệ")
    @Length(max = 200, message = "Tối đa 200 từ")
    private String jobName;

    @NotNull
    private MultipartFile imgJob;
}
