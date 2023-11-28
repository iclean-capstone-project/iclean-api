package iclean.code.data.dto.request.booking;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CreateBookingRequestNow {
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private Integer serviceUnitId;
    private String note;
    private Integer addressId;
    private Boolean usingPoint;
    private Boolean autoAssign;
}
