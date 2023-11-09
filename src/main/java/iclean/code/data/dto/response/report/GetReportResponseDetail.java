package iclean.code.data.dto.response.report;

import iclean.code.data.dto.response.booking.GetBookingResponseDetail;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetReportResponseDetail {
    private Integer reportId;
    private String bookingId;
    private String fullName;
    private String phoneNumber;
    private String reportTypeDetail;
    private String detail;
    private LocalDateTime createAt;
    private String reportStatus;
    private GetBookingResponseDetail booking;
}
