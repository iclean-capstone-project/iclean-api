package iclean.code.data.dto.request.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class UpdateUnitRequest {
    private String unitDetail;
    private Double unitValue;
    private String isActive;
    private MultipartFile fileUnit;
}
