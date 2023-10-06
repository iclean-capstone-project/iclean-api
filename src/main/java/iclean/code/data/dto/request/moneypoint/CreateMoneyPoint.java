package iclean.code.data.dto.request.moneypoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Digits;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMoneyPoint {
    @Range(min = 0, message = "currentPoint không được bé hơn 0")
    private Integer currentPoint;

    @Range(min = 0, message = "Tiền phải lớn hơn 0 VNĐ")
    private Integer currentMoney;

    @Range(min = 0, message = "userId phải lớn hơn 0 VNĐ")
    private Integer userId;

}