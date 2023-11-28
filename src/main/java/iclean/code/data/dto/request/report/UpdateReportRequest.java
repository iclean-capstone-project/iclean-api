package iclean.code.data.dto.request.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportRequest {

    @Length(max = 200, message = "Tối đa 200 từ")
    private String reason;

    @Min(value = 0, message = "Refund percent must be greater than 0")
    @Max(value = 100, message = "Refund percent must be smaller than 100")
    private Double refundPercent;
}
