package iclean.code.data.dto.request.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportRequest {

    @Length(max = 200, message = "Tối đa 200 từ")
    @NotNull(message = "Giải pháp không được để trống")
    @NotBlank(message = "Giải pháp không được để trống")
    private String solution;

    @Schema(example = "Money|Point")
    @Pattern(regexp = "(?i)(Money|Point)", message = "Option is not valid")
    private String option;

    @Length(max = 200, message = "Tối đa 200 từ")
    @NotNull(message = "reportStatus không được để trống")
    @NotBlank(message = "reportStatus không được để trống")
    private String reportStatus;

    @Range(min = 1000, message = "Giá tiền phải lớn hơn 1000 VNĐ")
    private Double refund;
}
