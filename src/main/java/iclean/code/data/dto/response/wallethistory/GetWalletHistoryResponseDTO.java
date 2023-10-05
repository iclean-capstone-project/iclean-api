package iclean.code.data.dto.response.wallethistory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import iclean.code.data.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
public class GetWalletHistoryResponseDTO {
    private Integer walletHistoryId;
    private Double balance;
    private LocalDateTime createAt;
    private String transactionStatus;
}
