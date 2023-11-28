package iclean.code.data.dto.response.transaction;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetTransactionResponse {
    private Integer transactionId;
    private String transactionCode;
    private Double amount;
    private LocalDateTime createAt;
    private String transactionStatus;
    private String transactionType;
    private String note;
}
