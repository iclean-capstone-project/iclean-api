package iclean.code.data.dto.response.service;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetServiceDetailResponse {
    private Integer serviceId;
    private String serviceName;
    private String description;
    private String serviceIcon;
    private LocalDateTime createAt;
    private Boolean isDeleted;
    private List<GetServiceImagesResponse> images;
}