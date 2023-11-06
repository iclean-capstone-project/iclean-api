package iclean.code.data.dto.response.booking;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetBookingResponse {

    private Integer bookingId;

    private String renterName;

    private String serviceName;

    private List<String> serviceImages;

    private LocalDateTime orderDate;

    private Integer requestCount;

    private Double totalPrice;

    private Double totalPriceActual;

    private LocalDateTime updateAt;

    private String bookingStatus;
}
