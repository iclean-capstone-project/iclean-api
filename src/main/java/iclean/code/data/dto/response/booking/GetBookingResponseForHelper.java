package iclean.code.data.dto.response.booking;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class GetBookingResponseForHelper {
    private Integer bookingDetailId;
    private Integer serviceUnitId;
    private Double equivalent;
    private String value;
    private String renterName;

    private String serviceName;

    private String serviceImages;

    private String workDate;

    private String workStart;

    private Double amount;

    private String locationDescription;

    private Double longitude;

    private Double latitude;

    private String noteMessage;
    private Boolean isApplied = false;
}
