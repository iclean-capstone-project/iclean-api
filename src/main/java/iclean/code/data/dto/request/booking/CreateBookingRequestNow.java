package iclean.code.data.dto.request.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateBookingRequestNow {
    private LocalDateTime startTime;
    private Integer serviceUnitId;
    private String note;
    private Integer addressId;
    private Boolean usingPoint;
    private Boolean autoAssign;
}
