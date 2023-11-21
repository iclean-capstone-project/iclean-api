package iclean.code.data.dto.response.address;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetAddressResponseDetail {
    private Integer addressId;

    private Double longitude;

    private Double latitude;

    private String description;

    private String locationName;

    private Boolean isDefault;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
}
