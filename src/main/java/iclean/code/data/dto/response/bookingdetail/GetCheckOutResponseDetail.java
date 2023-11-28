package iclean.code.data.dto.response.bookingdetail;

import lombok.Data;

import java.util.List;

@Data
public class GetCheckOutResponseDetail {
    private Integer cartId;
    private Double longitude;
    private Double latitude;
    private String locationDescription;
    private String locationName;
    private Double totalPrice;
    private Double totalPriceActual;
    private Boolean usingPoint;
    private Boolean autoAssign;
    private List<GetCartBookingDetailResponse> details;
}