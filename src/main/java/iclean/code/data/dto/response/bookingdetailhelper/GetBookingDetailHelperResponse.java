package iclean.code.data.dto.response.bookingdetailhelper;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class GetBookingDetailHelperResponse {
    private Integer serviceId;
    private String serviceName;
    private String serviceIcon;
    private LocalDateTime orderDate;
    private LocalDate workDate;
    private LocalTime workStart;
    private Double totalPrice;
    private List<GetHelpersResponse> helpers;
}
