package iclean.code.data.dto.request.authen;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TokenRefreshRequest {
    @NotBlank(message = "Refresh token cannot be empty")
    private String refreshToken;
}
