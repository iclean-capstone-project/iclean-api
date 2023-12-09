package iclean.code.data.dto.request.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class UpdateServiceRequest {
    private String serviceName;
    private String description;
    private String serviceStatus;
    private MultipartFile imgService;
}