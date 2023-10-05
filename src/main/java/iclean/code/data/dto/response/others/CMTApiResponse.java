package iclean.code.data.dto.response.others;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CMTApiResponse {
    private String status;
    private String message;

    @JsonProperty("data")
    private Object data;
}
