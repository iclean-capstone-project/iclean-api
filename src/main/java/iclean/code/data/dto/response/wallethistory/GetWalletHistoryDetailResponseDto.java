package iclean.code.data.dto.response.wallethistory;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class GetWalletHistoryDetailResponseDto {
    private Integer walletHistoryId;
    private Double balance;
    private String note;
    private LocalDateTime createAt;
    private String transactionStatus;
}
