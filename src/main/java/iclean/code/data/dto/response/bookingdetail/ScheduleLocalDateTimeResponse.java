package iclean.code.data.dto.response.bookingdetail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleLocalDateTimeResponse {
    private LocalDateTime start;
    private LocalDateTime end;
}
