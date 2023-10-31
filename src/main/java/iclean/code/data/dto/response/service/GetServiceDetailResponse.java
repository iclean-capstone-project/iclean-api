package iclean.code.data.dto.response.service;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetServiceDetailResponse {
    private Integer serviceId;
    private String serviceName;
    private String description;
    private String serviceImage;
    private LocalDateTime createAt;
    private Boolean isDeleted;
}
