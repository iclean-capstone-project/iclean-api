package iclean.code.data.dto.response.bookingstatushistory;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetBookingStatusHistoryResponse {
    private Integer statusHistoryId;
    private LocalDateTime createAt;
    private String bookingDetailStatus;
}
