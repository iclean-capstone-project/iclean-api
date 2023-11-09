package iclean.code.data.dto.response.workschedule;

import iclean.code.data.enumjava.DayOfWeekEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetWorkScheduleResponse {
    private DayOfWeekEnum dayOfWeekEnum;
    private List<TimeRangeOfDay> times;
}
