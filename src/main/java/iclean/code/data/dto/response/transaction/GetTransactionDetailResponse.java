package iclean.code.data.dto.response.transaction;

import iclean.code.data.dto.response.service.PriceService;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetTransactionDetailResponse {
    private Integer transactionId;
    private String transactionCode;
    private Double amount;
    private String note;
    private LocalDateTime createAt;
    private String transactionStatus;
    private String transactionType;
    private List<PriceService> priceServices;
}
