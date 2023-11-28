package iclean.code.data.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResultResponse {
    private String to;
    private String bookingCode;
    private String contentReport;
    private String status;
    private String solution;
    private String renterName;
    private String managerName;
}
