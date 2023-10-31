package iclean.code.data.dto.response.service;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetServiceResponse {
    private Integer serviceId;
    private String serviceName;
    private String serviceImage;
    private Boolean isDeleted;
    private LocalDateTime createAt;
}
