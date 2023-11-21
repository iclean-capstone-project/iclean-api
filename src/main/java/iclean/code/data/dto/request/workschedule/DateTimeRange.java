package iclean.code.data.dto.request.workschedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateTimeRange {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
