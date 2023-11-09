package iclean.code.data.dto.response.report;

import lombok.Data;

@Data
public class GetReportTypeResponse {
    private int reportTypeId;
    private String reportDetail;
}