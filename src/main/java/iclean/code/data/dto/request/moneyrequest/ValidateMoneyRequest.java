package iclean.code.data.dto.request.moneyrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateMoneyRequest {
    private String phoneNumber;
    private String otpToken;
}
