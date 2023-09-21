package iclean.code.data.dto.request;

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
    private String accessToken;
}
