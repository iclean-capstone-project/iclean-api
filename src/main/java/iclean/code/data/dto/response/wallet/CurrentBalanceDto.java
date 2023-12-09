package iclean.code.data.dto.response.wallet;

import lombok.Data;

@Data
public class CurrentBalanceDto {
    private String walletType;
    private Double currentBalance;
}