package iclean.code.data.dto.request.wallet;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalance {

    @Range(min = 0, message = "Balance cannot less than 0")
    private Integer balance;

    @Schema(example = "money|point")
    @Pattern(regexp = "(?i)(Money|Point)", message = "Wallet Type is not valid")
    @NotNull(message = "Wallet Type are invalid")
    private String walletType;
}
