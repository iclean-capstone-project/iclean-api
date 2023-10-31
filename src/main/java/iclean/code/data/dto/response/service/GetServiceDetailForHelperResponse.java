package iclean.code.data.dto.response.service;

import iclean.code.data.dto.response.serviceunit.GetServiceUnitResponseForHelper;
import lombok.Data;

import java.util.List;

@Data
public class GetServiceDetailForHelperResponse {
    private Integer serviceId;
    private String serviceName;
    private String description;
    private String serviceImage;
    private List<GetServiceUnitResponseForHelper> details;
}
