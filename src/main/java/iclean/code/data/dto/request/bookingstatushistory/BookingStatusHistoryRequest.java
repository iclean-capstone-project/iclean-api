package iclean.code.data.dto.request.bookingstatushistory;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingStatusHistoryRequest {
    private LocalDateTime createAt;

    private Integer bookingId;

    private Integer bookingStatusId;
}
