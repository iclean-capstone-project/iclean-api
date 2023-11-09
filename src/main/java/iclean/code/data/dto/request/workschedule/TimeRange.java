package iclean.code.data.dto.request.workschedule;

import lombok.Data;

import java.time.LocalTime;

@Data
public class TimeRange {
    private LocalTime startTime;
    private LocalTime endTime;
}
