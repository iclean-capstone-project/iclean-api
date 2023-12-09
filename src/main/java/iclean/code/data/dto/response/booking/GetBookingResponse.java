package iclean.code.data.dto.response.booking;

import iclean.code.utils.anotation.SortValue;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetBookingResponse {

    private Integer bookingId;

    private String bookingCode;

    @SortValue(value = "renter.fullname")
    private String renterName;

    @SortValue(value = "renter.avatar")
    private String renterAvatar;

    @SortValue(value = "renter.phoneNumber")
    private String renterPhoneNumber;

    private String serviceNames;

    private String serviceAvatar;

    private LocalDateTime orderDate;

    private Integer requestCount;

    private Double totalPrice;

    private Double totalPriceActual;

    private LocalDateTime updateAt;

    private String bookingStatus;

    private String managerName;
}
