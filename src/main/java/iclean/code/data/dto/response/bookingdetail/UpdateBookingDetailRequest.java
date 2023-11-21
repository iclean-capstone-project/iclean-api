package iclean.code.data.dto.response.bookingdetail;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateBookingDetailRequest {
    private LocalDateTime startTime;
    private String note;
}
