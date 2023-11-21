package iclean.code.data.dto.request.workschedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeRangeOfDayRequest {
        private LocalTime startTime;
        private LocalTime endTime;
}
