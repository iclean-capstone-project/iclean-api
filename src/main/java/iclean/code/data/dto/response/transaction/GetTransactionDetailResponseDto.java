package iclean.code.data.dto.response.transaction;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class GetTransactionDetailResponseDto {
    private Integer transactionId;
    private Double balance;
    private String note;
    private LocalDateTime createAt;
    private String transactionStatus;
}
