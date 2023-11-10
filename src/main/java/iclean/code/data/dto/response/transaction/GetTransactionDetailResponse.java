package iclean.code.data.dto.response.transaction;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class GetTransactionDetailResponse {
    private Integer transactionId;
    private Double amount;
    private String note;
    private LocalDateTime createAt;
    private String transactionStatus;
}
