package iclean.code.data.dto.request.booking;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CheckOutBookingRequestNow {
    private LocalDateTime startTime;
    @NotNull
    private Integer serviceUnitId;
    private String note;
    private Integer addressId;
    @NotNull
    private Boolean usingPoint;
    @NotNull
    private Boolean autoAssign;
}
