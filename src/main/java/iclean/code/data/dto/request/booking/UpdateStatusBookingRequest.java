package iclean.code.data.dto.request.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.annotation.Nullable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusBookingRequest {

    private String bookingStatus;

    @Nullable
    private Integer rejectReasonId;
}
