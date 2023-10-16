package iclean.code.data.dto.response.others;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BackCCCD {
    private String features;
    @JsonProperty("issue_date")
    private String issueDate;
    @JsonProperty("issue_loc")
    private String issueLoc;
    @JsonProperty("type_new")
    private String typeNew;
}
