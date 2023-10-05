package iclean.code.data.dto.request.authen;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FcmTokenDto {
    @NotBlank(message = "FcmToken cannot be null")
    @NotBlank(message = "FcmToken cannot be empty")
    @Schema(example = "1f8i7tsa23evsdfdf")
    private String fcmToken;
}
