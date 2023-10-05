package iclean.code.data.dto.request.wallethistory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateWalletHistoryRequestDTO {

    @NotNull(message = "Balance là bắt buộc")
    @Schema(example = "0.00")
    @Min(value = 1, message = "Balance phải lớn hơn không")
    private Double balance;

    @NotNull(message = "Balance là bắt buộc")
    @NotBlank(message = "Ghi chú không để trống")
    @Schema(example = "Trừ tiền thuê dịch vụ quét nhà")
    @NotBlank(message = "Ghi chú không để trống")
    private String note;

    @NotNull(message = "Balance là bắt buộc")
    @NotBlank(message = "Status không để trống")
    @Pattern(regexp = "^(Success|Fail)$", message = "Status sai định dạng")
    @Schema(example = "Success|Fail")
    private String transactionStatus;
}
