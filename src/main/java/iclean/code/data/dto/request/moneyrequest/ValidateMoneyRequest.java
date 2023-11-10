package iclean.code.data.dto.request.moneyrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateMoneyRequest {
    private Integer requestId;
    private String otpToken;
}
