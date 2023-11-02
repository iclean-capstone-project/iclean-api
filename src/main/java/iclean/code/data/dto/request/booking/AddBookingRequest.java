package iclean.code.data.dto.request.booking;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBookingRequest {

    private String startTime;

    private Integer serviceUnitId;

}
