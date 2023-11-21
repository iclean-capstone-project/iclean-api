package iclean.code.data.dto.request.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutCartRequest {
    private Integer addressId;
    private Boolean usingPoint = false;
    private Boolean autoAssign = false;
}
