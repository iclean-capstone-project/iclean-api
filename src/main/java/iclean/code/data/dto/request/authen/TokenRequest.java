package iclean.code.data.dto.request.authen;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {

    @NotNull(message = "Access Token cannot be null")
    @NotBlank(message = "Access Token is empty")
    @Schema(example = "abcxyz")
    private String accessToken;
}
