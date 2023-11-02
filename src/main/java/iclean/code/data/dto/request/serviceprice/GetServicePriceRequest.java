package iclean.code.data.dto.request.serviceprice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetServicePriceRequest {
    private Integer serviceUnitId;
    private String startTime;
}
