package iclean.code.data.dto.response.serviceprice;

import lombok.Data;

@Data
public class GetServicePriceResponse {
    private Integer id;
    private String startTime;
    private String endTime;
    private Double price;
    private Double employeeCommission;
}
