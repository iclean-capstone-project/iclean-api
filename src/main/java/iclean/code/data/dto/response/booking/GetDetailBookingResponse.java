package iclean.code.data.dto.response.booking;

import iclean.code.data.dto.response.bookingdetail.GetBookingDetailResponse;
import iclean.code.data.dto.response.bookingstatushistory.GetBookingStatusHistoryResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetDetailBookingResponse {
    private Integer bookingId;
    private Double latitude;
    private Double longitude;
    private String locationDescription;
    private LocalDateTime orderDate;
    private Double totalPrice;
    private Double totalPriceActual;
    private Integer requestCount;
    private String rejectionReasonContent;
    private String rejectionReasonDescription;
    private String bookingCode;
    private String renterName;
    private String currentStatus;
    private List<GetBookingStatusHistoryResponse> statuses;
    private List<GetBookingDetailResponse> details;
}
