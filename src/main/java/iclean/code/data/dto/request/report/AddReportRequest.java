package iclean.code.data.dto.request.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddReportRequest {
    @Range(min = 1, message = "bookingId phải lớn hơn 1")
    private Integer bookingId;

    @Range(min = 1, message = "reportTypeId phải lớn hơn 1")
    private Integer reportTypeId;

    @Length(max = 200, message = "Tối đa 200 từ")
    @NotNull(message = "detail không được để trống")
    @NotBlank(message = "detail không được để trống")
    private String detail;

    @Length(max = 200, message = "Tối đa 200 từ")
    @NotNull(message = "reportStatus không được để trống")
    @NotBlank(message = "reportStatus không được để trống")
    private String reportStatus;
}
