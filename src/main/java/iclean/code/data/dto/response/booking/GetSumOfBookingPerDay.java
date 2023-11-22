package iclean.code.data.dto.response.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class GetSumOfBookingPerDay {
    private Date dayOfMonth;

    private Long  bookingCounter;

    private Double bookingSalesInMonth;
}
