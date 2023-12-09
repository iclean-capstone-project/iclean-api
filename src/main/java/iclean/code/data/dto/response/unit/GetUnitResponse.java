package iclean.code.data.dto.response.unit;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetUnitResponse {
    private Integer unitId;
    private String unitDetail;
    private String unitValue;
    private Boolean isDeleted;
    private LocalDateTime createAt;
}
