package iclean.code.data.dto.response.address;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetAddressResponseDto {
    private Integer addressId;
    private String description;
    private String street;
    private String locationName;
    private Boolean isDefault;
    private LocalDateTime updateAt;
}
