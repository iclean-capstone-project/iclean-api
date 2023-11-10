package iclean.code.data.dto.request.booking;

import lombok.Data;

@Data
public class CheckOutCartRequest {
    private Integer addressId;
    private Boolean usingPoint;
    private Boolean autoAssign;
}
