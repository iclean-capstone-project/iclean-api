package iclean.code.data.dto.request.booking;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBookingRequest {

    private LocalDateTime startTime;

    private Integer serviceUnitId;

    private String note;
}
