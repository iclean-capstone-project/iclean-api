package iclean.code.data.dto.request.serviceunit;

import iclean.code.data.dto.request.serviceprice.ServicePriceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateServiceUnitRequest {
    @NotNull(message = "Default Price is required")
    @Schema(example = "100000")
    @Range(min = 10000, max = 10000000, message = "Helper commission cannot be greater than 10 000 000 and smaller than 10 000")
    private Double defaultPrice;
    @NotNull(message = "Helper Commission is required")
    @Schema(example = "65")
    @Range(min = 1, max = 95, message = "Helper commission cannot be greater than 70 and smaller than 1")
    private Double helperCommission;
    @NotNull(message = "Unit is required")
    @Schema(example = "1")
    @Min(value = 1, message = "Unit id cannot be smaller than 1")
    private Integer unitId;
    @NotNull(message = "Service is required")
    @Schema(example = "1")
    @Min(value = 1, message = "Service id cannot be smaller than 1")
    private Integer serviceId;

    private List<ServicePriceRequest> servicePrices;
}