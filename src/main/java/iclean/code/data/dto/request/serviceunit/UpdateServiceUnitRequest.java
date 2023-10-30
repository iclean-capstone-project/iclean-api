package iclean.code.data.dto.request.serviceunit;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class UpdateServiceUnitRequest {

    private Double priceDefault;

    private Double employeeCommission;

    private MultipartFile imgUnitFile;

    private String unitValue;
}
