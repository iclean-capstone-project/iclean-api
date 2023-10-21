package iclean.code.data.dto.response.transaction;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetTransactionResponseDto {
    private Integer transactionId;
    private Double balance;
    private LocalDateTime createAt;
    private String transactionStatus;
}
