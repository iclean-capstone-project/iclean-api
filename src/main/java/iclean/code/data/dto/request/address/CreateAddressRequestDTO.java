package iclean.code.data.dto.request.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class CreateAddressRequestDTO {
    @NotNull(message = "Kinh độ là bắt buộc")
    @NotBlank(message = "Kinh độ không được bỏ trống")
    @Schema(example = "0.00")
    @Pattern(regexp = "[-+]?[0-9]*\\\\.?[0-9]+([eE][-+]?[0-9]+)?", message = "Kinh độ sai định dạng")
    private Double longitude;

    @NotNull(message = "Vĩ độ là bắt buộc")
    @NotBlank(message = "Vĩ độ không được bỏ trống")
    @Schema(example = "0.00")
    @Pattern(regexp = "[-+]?[0-9]*\\\\.?[0-9]+([eE][-+]?[0-9]+)?", message = "Vĩ độ sai định dạng")
    private Double latitude;

    @Schema(example = "903, đường Võ Văn Ngân, Thành phố Thủ Đức, Thành phố Hồ Chí Minh")
    private String description;

    @Schema(example = "Võ Văn Ngân")
    private String street;

    @Schema(example = "Nhà riêng")
    private String locationName;

    @Schema(example = "false")
    @Pattern(regexp = "^(true|false)$", message = "isDefault sai định dạng")
    private Boolean isDefault;

    @NotNull(message = "user Id là bắt buộc")
    @NotBlank(message = "user Id không được bỏ trống")
    @Schema(example = "1")
    private Integer userId;
}
