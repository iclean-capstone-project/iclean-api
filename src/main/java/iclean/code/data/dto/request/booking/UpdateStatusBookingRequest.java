package iclean.code.data.dto.request.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusBookingRequest {

    @Range(min = 1, message = "Mã đơn phải lớn hơn 1")
    private int bookingId;

    @Range(min = 1, max = 4, message = "bookingStatusId Nhập trong khoảng 1-4")
    private int bookingStatusId;
}
