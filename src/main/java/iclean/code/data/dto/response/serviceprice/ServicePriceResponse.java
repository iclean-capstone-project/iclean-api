package iclean.code.data.dto.response.serviceprice;

import lombok.Data;

import java.util.List;

@Data
public class ServicePriceResponse {
    private Integer serviceUnitId;
    private List<GetServicePriceResponse> responses;
}
