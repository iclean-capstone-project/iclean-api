package iclean.code.data.dto.request.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusBookingRequest {

    @Range(min = 1, max = 4, message = "bookingStatusId Nhập trong khoảng 1-4")
    private Integer bookingStatusId;
}
