package iclean.code.data.dto.response.bookingdetail;

import iclean.code.data.dto.response.booking.GetTransactionBookingResponse;
import iclean.code.data.dto.response.bookingdetailhelper.GetHelpersResponse;
import iclean.code.data.dto.response.bookingstatushistory.GetBookingStatusHistoryResponse;
import iclean.code.data.dto.response.feedback.GetFeedbackResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetBookingDetailDetailResponse {
    private Integer bookingDetailId;
    private String bookingCode;
    private LocalDateTime orderDate;
    private Integer serviceId;
    private Integer serviceUnitId;
    private String serviceName;
    private String serviceIcon;
    private String workDate;
    private String note;
    private String workStart;
    private String value;
    private Double equivalent;
    private Double price;
    private Double refundMoney;
    private Double refundPoint;
    private Double penaltyMoney;
    private String currentStatus;
    private String rejectionReasonContent;
    private String rejectionReasonDescription;
    private GetAddressResponseBooking address;
    private GetHelpersResponse helper;
    private GetTransactionBookingResponse transaction;
    private GetFeedbackResponse feedback;
    private List<GetBookingStatusHistoryResponse> statuses;
    private Boolean reported;
}
