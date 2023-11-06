package iclean.code.data.dto.response.booking;

import iclean.code.data.dto.response.bookingdetail.GetBookingDetailResponse;
import lombok.Data;

import java.util.List;

@Data
public class GetCartResponseDetail {
    private Integer cartId;
    private Double totalPrice;
    private Double totalPriceActual;
    private List<GetBookingDetailResponse> details;
}
