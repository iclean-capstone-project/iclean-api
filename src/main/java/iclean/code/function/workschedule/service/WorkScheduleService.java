package iclean.code.function.workschedule.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.workschedule.CreateWorkScheduleRequest;
import iclean.code.data.dto.request.workschedule.UpdateWorkScheduleRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WorkScheduleService {
    ResponseEntity<ResponseObject> getWorkSchedules(int helperId);

    ResponseEntity<ResponseObject> createAllWorkSchedule(int helperId, List<CreateWorkScheduleRequest> requests);

    ResponseEntity<ResponseObject> createWorkSchedule(int helperId, CreateWorkScheduleRequest request);

    ResponseEntity<ResponseObject> updateWorkSchedule(int helperId, int id, UpdateWorkScheduleRequest request);

    ResponseEntity<ResponseObject> deleteWorkSchedule(int helperId, int workScheduleId);
}
