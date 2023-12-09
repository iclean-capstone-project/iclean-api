package iclean.code.data.dto.response.serviceunit;

import lombok.Data;

@Data
public class GetServiceUnitResponseForHelper {
    private Integer serviceUnitId;
    private Double helperCommissionPrice;
    private String valueUnit;
    private String serviceUnitImage;
}
