package iclean.code.data.dto.request.serviceprice;

import lombok.Data;

@Data
public class ServicePriceRequest {
    private Integer id;
    private Double price;
    private Double employeeCommission;
}
