package iclean.code.data.dto.response.others;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FrontCCCD {
    private String id;
    private String name;
    private String dob;
    private String sex;
    private String home;
    private String address;
    private String doe;
    @JsonProperty("type_new")
    private String typeNew;
}

