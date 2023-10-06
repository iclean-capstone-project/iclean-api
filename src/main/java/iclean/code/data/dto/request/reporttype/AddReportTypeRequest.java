package iclean.code.data.dto.request.reporttype;

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
public class AddReportTypeRequest {
    @NotNull(message = "Loại báo cáo không được trống")
    @NotBlank(message = "Loại báo cáo không được trống")
    @Length(max = 200, message = "Tối đa 200 từ")
    private String reportDetail;
}
