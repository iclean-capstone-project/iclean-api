package iclean.code.data.dto.request.serviceunit;

import iclean.code.data.dto.request.serviceprice.ServicePriceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class UpdateServiceUnitRequest {
    @Schema(example = "100000")
    @Range(min = 10000, max = 10000000, message = "Helper commission cannot be greater than 10 000 000 and smaller than 10 000")
    private Double defaultPrice;
    @Schema(example = "65")
    @Range(min = 1, max = 95, message = "Helper commission cannot be greater than 70 and smaller than 1")
    private Double helperCommission;
    @Pattern(regexp = "(?i)(Active)", message = "Status are invalid")
    @Schema(example = "Active")
    private String serviceUnitStatus;
    private List<ServicePriceRequest> servicePriceRequests;
}