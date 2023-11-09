package iclean.code.data.dto.request.workschedule;

import lombok.Data;

@Data
public class CreateWorkScheduleRequest {
    private String dayOfWeek;
    private String startTime;
    private String endTime;
}
