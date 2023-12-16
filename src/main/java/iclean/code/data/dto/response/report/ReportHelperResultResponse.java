package iclean.code.data.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportHelperResultResponse {
    private String to;
    private String bookingCode;
    private String contentReport;
    private String solution;
    private String helperName;
    private String managerName;
    private Double moneyPen;
    private LocalDateTime createAt;
}
