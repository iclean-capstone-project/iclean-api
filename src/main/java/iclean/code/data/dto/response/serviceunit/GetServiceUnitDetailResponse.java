package iclean.code.data.dto.response.serviceunit;

import iclean.code.data.dto.response.serviceprice.GetServicePriceResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetServiceUnitDetailResponse {
    private Integer serviceUnitId;
    private Double defaultPrice;
    private Double helperCommission;
    private Boolean isDeleted;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private List<GetServicePriceResponse> servicePrices;
}
