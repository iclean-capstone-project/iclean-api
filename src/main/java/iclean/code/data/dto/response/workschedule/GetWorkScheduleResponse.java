package iclean.code.data.dto.response.workschedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetWorkScheduleResponse {
    private DayOfWeek dayOfWeekEnum;
    private List<TimeRangeOfDay> times;
}
