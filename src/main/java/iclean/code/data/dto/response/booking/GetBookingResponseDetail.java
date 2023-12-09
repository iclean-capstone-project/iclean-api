package iclean.code.data.dto.response.booking;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetBookingResponseDetail {
    private Integer cartId;
    private Double totalPrice;
    private Double totalPriceActual;
    private String renterName;
    private String renterId;
    private String renterPhoneNumber;
    private String renterAvatar;
    private LocalDateTime orderDate;
    private List<GetBookingDetailAfterCartResponse> details;
}
