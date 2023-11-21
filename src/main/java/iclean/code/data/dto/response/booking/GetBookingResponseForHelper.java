package iclean.code.data.dto.response.booking;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class GetBookingResponseForHelper {
    private Integer bookingDetailId;

    private String renterName;

    private String serviceName;

    private String serviceImages;

    private LocalDate workDate;

    private LocalTime workStart;

    private Double amount;

    private String locationDescription;

    private Double longitude;

    private Double latitude;

    private String noteMessage;
}
