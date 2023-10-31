package iclean.code.data.dto.request.service;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class CreateServiceRequest {
    private String serviceName;
    private String description;
    private MultipartFile imgService;
}
