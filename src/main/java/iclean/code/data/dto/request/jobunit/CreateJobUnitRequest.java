package iclean.code.data.dto.request.jobunit;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
public class CreateJobUnitRequest {

    @NotNull(message = "Price Default is required")
    @Schema(example = "10000")
    @Min(value = 20000, message = "Price Default cannot be smaller than 20000")
    @Max(value = 500000, message = "Price Default cannot be greater than 500000")
    private Double priceDefault;

    @NotNull(message = "Employee Commission is required")
    @Schema(example = "30")
    @Min(value = 10, message = "Employee Commission cannot be smaller than 10")
    @Max(value = 70, message = "Employee Commission cannot be greater than 70")
    private Double employeeCommission;

    @NotNull(message = "Unit Value cannot be null")
    @NotBlank(message = "Unit Value is empty")
    @Pattern(regexp = "^[0-9a-zA-Z/]+$",message = "Unit Value is invalid")
    @Length(max = 200, message = "Max length: 200")
    private String unitValue;
}
