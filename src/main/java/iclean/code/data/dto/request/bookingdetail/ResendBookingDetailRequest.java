package iclean.code.data.dto.request.bookingdetail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResendBookingDetailRequest {
    private LocalDateTime startTime;
    private String note;
    private Integer addressId;
    private Boolean usingPoint = false;
    private Boolean autoAssign = false;
}
