package iclean.code.data.dto.request.wallethistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetWalletHistoryRequestDTO {
    private Integer userId;
}
