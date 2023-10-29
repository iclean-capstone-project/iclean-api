package iclean.code.data.dto.request.jobunit;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class UpdateJobUnitRequest {

    private Double priceDefault;

    private Double employeeCommission;

    private MultipartFile imgUnitFile;

    private String unitValue;
}
