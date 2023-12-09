package iclean.code.function.workschedule.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.workschedule.CreateWorkScheduleRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WorkScheduleService {
    ResponseEntity<ResponseObject> getWorkSchedules(int helperId);
    ResponseEntity<ResponseObject> updateWorkSchedule(int helperId, List<CreateWorkScheduleRequest> requests);
}
