package iclean.code.data.dto.response.workschedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeRangeOfDay {
    private Integer workScheduleId;
    private LocalTime startTime;
    private LocalTime endTime;
}