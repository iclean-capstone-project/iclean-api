package iclean.code.data.dto.response.address;

import iclean.code.utils.anotation.SortValue;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetAddressResponse {
    @SortValue("addressName")
    private Integer addressId;
    private String description;
    private String locationName;
    private Boolean isDefault;
    private LocalDateTime createAt;
}
