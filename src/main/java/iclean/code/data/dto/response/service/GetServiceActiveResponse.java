package iclean.code.data.dto.response.service;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetServiceActiveResponse {
    private Integer serviceId;
    private String serviceName;
    private String serviceIcon;
    private LocalDateTime createAt;
}
