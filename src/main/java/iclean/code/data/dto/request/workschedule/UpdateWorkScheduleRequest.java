package iclean.code.data.dto.request.workschedule;

import lombok.Data;

@Data
public class UpdateWorkScheduleRequest {
    private String startTime;
    private String endTime;
}
