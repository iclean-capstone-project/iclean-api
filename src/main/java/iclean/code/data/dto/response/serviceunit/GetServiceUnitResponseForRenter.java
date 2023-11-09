package iclean.code.data.dto.response.serviceunit;

import lombok.Data;

@Data
public class GetServiceUnitResponseForRenter {
    private Integer serviceUnitId;
    private Double defaultPrice;
    private String value;
    private Double equivalent;
    private String serviceUnitImage;
}
