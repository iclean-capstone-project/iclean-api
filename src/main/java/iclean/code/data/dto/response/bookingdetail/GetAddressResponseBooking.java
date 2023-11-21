package iclean.code.data.dto.response.bookingdetail;

import lombok.Data;

@Data
public class GetAddressResponseBooking {
    private String locationDescription;
    private String locationName;
    private Double longitude;
    private Double latitude;
}