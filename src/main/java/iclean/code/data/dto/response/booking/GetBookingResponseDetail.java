package iclean.code.data.dto.response.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetBookingResponseDetail {
    private Integer bookingId;

    private String renterName;

    private String employeeName;

    private String jobName;

    private LocalDateTime orderDate;

    private Double totalPrice;

    private String bookingStatus;
}
