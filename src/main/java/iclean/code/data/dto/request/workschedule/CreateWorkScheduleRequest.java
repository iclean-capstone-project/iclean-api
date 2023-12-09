package iclean.code.data.dto.request.workschedule;

import lombok.Data;

import java.util.List;

@Data
public class CreateWorkScheduleRequest {
    private String dayOfWeekEnum;
    private List<TimeRangeOfDayRequest> times;
}
