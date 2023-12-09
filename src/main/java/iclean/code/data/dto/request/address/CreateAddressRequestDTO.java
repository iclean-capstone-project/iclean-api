package iclean.code.data.dto.request.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CreateAddressRequestDTO {
    @NotNull(message = "Kinh độ là bắt buộc")
    @Min(value = -180, message = "Kinh độ không nhỏ hơn -180")
    @Max(value = 180, message = "Kinh độ không lớn hơn 180")
    @Schema(example = "0.00")
    private Double longitude;

    @NotNull(message = "Vĩ độ là bắt buộc")
    @Schema(example = "0.00")
    @Min(value = -180, message = "Vĩ độ không nhỏ hơn -180")
    @Max(value = 180, message = "Vĩ độ không lớn hơn 180")
    private Double latitude;

    @Schema(example = "903, đường Võ Văn Ngân, Thành phố Thủ Đức, Thành phố Hồ Chí Minh")
    private String description;

    @Schema(example = "Nhà riêng")
    private String locationName;

    @Schema(example = "false")
    private Boolean isDefault;
}
