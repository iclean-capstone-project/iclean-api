package iclean.code.data.dto.response.booking;

import iclean.code.data.dto.response.service.PriceService;
import lombok.Data;

import java.util.List;

@Data
public class GetTransactionBookingResponse {
    private String transactionCode;
    private List<PriceService> servicePrice;
    private Double totalPrice;
    private Double discount;
    private Double totalPriceActual;
    private String status;
}
