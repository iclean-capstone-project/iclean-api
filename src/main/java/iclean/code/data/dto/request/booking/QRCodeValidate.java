package iclean.code.data.dto.request.booking;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class QRCodeValidate {
    @NotNull(message = "QR Code cannot be null")
    @NotBlank(message = "QR Code cannot be empty")
    @Pattern(regexp = "\\d{6,}$", message = "QR Code are invalid")
    @Schema(example = "123456")
    private String qrCode;
}