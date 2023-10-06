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
public class UpdateReportRequest {
//    private Integer bookingId;
//
//    private Integer reportTypeId;

//    private String detail;

    @Length(max = 200, message = "Tối đa 200 từ")
    @NotNull(message = "Giải pháp không được để trống")
    @NotBlank(message = "Giải pháp không được để trống")
    private String solution;

    @Range(min = 1, max = 100, message = "Phần trăm refund không được lớn hơn 100 tiếng")
    private Double refundPercent;

    @Length(max = 200, message = "Tối đa 200 từ")
    @NotNull(message = "reportStatus không được để trống")
    @NotBlank(message = "reportStatus không được để trống")
    private String reportStatus;

    @Range(min = 1000, message = "Giá tiền phải lớn hơn 1000 VNĐ")
    private Double refund;

//    private LocalDateTime createAt;

//    private LocalDateTime processAt;
}
