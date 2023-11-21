package iclean.code.data.dto.request.reporttype;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportType {
    @NotNull(message = "Loại báo cáo không được trống")
    @NotBlank(message = "Loại báo cáo không được trống")
    @Length(max = 200, message = "Tối đa 200 từ")
    private String reportDetail;
}
