package iclean.code.data.dto.response.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetBookingResponse {

    private Integer bookingId;

    private String renterName;

    private String employeeName;

    private String jobName;

    private LocalDateTime orderDate;

    private Integer requestCount;

    private Double totalPrice;

    private LocalDateTime updateAt;

    private String bookingStatus;

}
