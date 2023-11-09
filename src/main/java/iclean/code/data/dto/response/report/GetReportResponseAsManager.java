package iclean.code.data.dto.response.report;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetReportResponseAsManager {
    private Integer reportId;
    private String fullName;
    private String phoneNumber;
    private String reportTypeDetail;
    private String detail;
    private LocalDateTime createAt;
    private String reportStatus;
}
