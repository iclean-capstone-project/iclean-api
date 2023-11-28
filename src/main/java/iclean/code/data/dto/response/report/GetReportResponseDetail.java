package iclean.code.data.dto.response.report;

import iclean.code.data.dto.response.booking.GetBookingResponseDetail;
import iclean.code.data.dto.response.bookingdetail.GetBookingDetailDetailResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetReportResponseDetail {
    private Integer reportId;
    private Integer bookingDetailId;
    private String fullName;
    private String phoneNumber;
    private String reportTypeDetail;
    private String detail;
    private LocalDateTime createAt;
    private String reportStatus;
    private List<ReportAttachmentResponse> attachmentResponses;
    private GetBookingDetailDetailResponse bookingDetail;
}
