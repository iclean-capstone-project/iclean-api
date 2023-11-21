package iclean.code.data.dto.request.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    @NotNull(message = "Balance cannot be null")
    @Schema(example = "0.00")
    @Min(value = 1, message = "Balance cannot less than 1")
    private Double balance;

    @NotNull(message = "Note cannot be null")
    @Schema(example = "Trừ tiền thuê dịch vụ quét nhà")
    @NotBlank(message = "Note cannot be empty")
    private String note;

    @NotNull(message = "User ID cannot be null")
    @Schema(example = "1")
    private Integer userId;

    @NotNull(message = "Transaction Type cannot be null")
    @NotBlank(message = "Transaction Type cannot be empty")
    @Pattern(regexp = "(?i)(Deposit|Withdraw)", message = "Transaction Type are not valid")
    @Schema(example = "Deposit|Withdraw")
    private String transactionType;

    @NotNull(message = "Wallet Type cannot be null")
    @NotBlank(message = "Wallet Type cannot be empty")
    @Pattern(regexp = "(?i)(Money|Point)", message = "Wallet Type are not valid")
    @Schema(example = "Money|Point")
    private String walletType;

    private Integer bookingId;

    public TransactionRequest(Double balance, String note, Integer userId, String transactionType, String walletType) {
        this.balance = balance;
        this.note = note;
        this.userId = userId;
        this.transactionType = transactionType;
        this.walletType = walletType;
    }
}
