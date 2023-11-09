package iclean.code.data.dto.response.serviceregistration;

import iclean.code.data.enumjava.ServiceHelperStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetServiceOfHelperResponse {
    private Integer serviceRegistrationId;
    private String serviceName;
    private String serviceIcon;
    private LocalDateTime createAt;
    private ServiceHelperStatusEnum status;
}
