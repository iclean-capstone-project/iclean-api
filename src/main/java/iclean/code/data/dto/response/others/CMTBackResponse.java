package iclean.code.data.dto.response.others;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CMTBackResponse {
    private String errorCode;
    private String errorMessage;

    @JsonProperty("data")
    private List<BackCCCD> data;
}
