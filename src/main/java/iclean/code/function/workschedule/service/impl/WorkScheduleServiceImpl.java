package iclean.code.function.workschedule.service.impl;

import iclean.code.data.domain.HelperInformation;
import iclean.code.data.domain.WorkSchedule;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.workschedule.CreateWorkScheduleRequest;
import iclean.code.data.dto.request.workschedule.TimeRange;
import iclean.code.data.dto.request.workschedule.UpdateWorkScheduleRequest;
import iclean.code.data.dto.response.workschedule.GetWorkScheduleResponse;
import iclean.code.data.dto.response.workschedule.TimeRangeOfDay;
import iclean.code.data.enumjava.DayOfWeekEnum;
import iclean.code.data.repository.HelperInformationRepository;
import iclean.code.data.repository.WorkScheduleRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.workschedule.service.WorkScheduleService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class WorkScheduleServiceImpl implements WorkScheduleService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Autowired
    private HelperInformationRepository helperInformationRepository;
    @Override
    public ResponseEntity<ResponseObject> getWorkSchedules(int userId) {
        try {
            List<WorkSchedule> workSchedules = workScheduleRepository.findAllByUserId(userId);
            List<GetWorkScheduleResponse> responses = mappingFromDomain(workSchedules);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Get work schedules successful!", responses));
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createAllWorkSchedule(int userId, List<CreateWorkScheduleRequest> requests) {
        try {
            HelperInformation helperInformation = helperInformationRepository.findByUserId(userId);
            List<WorkSchedule> workSchedules = workScheduleRepository.findAllByUserId(userId);
            workScheduleRepository.deleteAll(workSchedules);
            List<WorkSchedule> newWorkSchedules = requests
                    .stream()
                    .map(value -> {
                        WorkSchedule workSchedule = modelMapper.map(value, WorkSchedule.class);
                        workSchedule.setDayOfWeek(DayOfWeekEnum.valueOf(value.getDayOfWeek().toUpperCase()));
                        workSchedule.setHelperInformation(helperInformation);
                        return workSchedule;
                    })
                    .collect(Collectors.toList());
            return checkOverLap(newWorkSchedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createWorkSchedule(int userId, CreateWorkScheduleRequest request) {
        try {
            WorkSchedule workSchedule = modelMapper.map(request, WorkSchedule.class);
            workSchedule.setDayOfWeek(DayOfWeekEnum.valueOf(request.getDayOfWeek().toUpperCase()));
            HelperInformation helperInformation = helperInformationRepository.findByUserId(userId);
            workSchedule.setHelperInformation(helperInformation);
            return checkOverLap(userId, workSchedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateWorkSchedule(int userId, int id, UpdateWorkScheduleRequest request) {
        try {
            WorkSchedule workSchedule = findById(id);
            isPermission(userId, workSchedule);
            modelMapper.map(request, workSchedule);
            return checkOverLap(userId, workSchedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    @NotNull
    private ResponseEntity<ResponseObject> checkOverLap(List<WorkSchedule> workSchedules) {
        List<GetWorkScheduleResponse> dtoList = mappingFromDomain(workSchedules);
        boolean checkOverLap;
        for (GetWorkScheduleResponse element :
                dtoList) {
            List<TimeRange> timeRange = element.getTimes()
                    .stream()
                    .map(value -> modelMapper.map(value, TimeRange.class))
                    .collect(Collectors.toList());
            checkOverLap = Utils.hasOverlapTime(timeRange);
            if (checkOverLap)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                "Hours are overlapping",
                                String.format("Hours are overlapping at %s", element.getDayOfWeekEnum())));
        }
        workScheduleRepository.saveAll(workSchedules);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(),
                        "Get work schedules successful!", null));
    }
    @NotNull
    private ResponseEntity<ResponseObject> checkOverLap(int userId, WorkSchedule workSchedule) {
        List<WorkSchedule> workSchedules = workScheduleRepository.findAllByUserId(userId);
        workSchedules.add(workSchedule);
        List<GetWorkScheduleResponse> dtoList = mappingFromDomain(workSchedules);
        boolean checkOverLap;
        for (GetWorkScheduleResponse element :
                dtoList) {
            List<TimeRange> timeRange = element.getTimes()
                    .stream()
                    .map(value -> modelMapper.map(value, TimeRange.class))
                    .collect(Collectors.toList());
            checkOverLap = Utils.hasOverlapTime(timeRange);
            if (checkOverLap)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                "Hours are overlapping",
                                String.format("Hours are overlapping at %s", element.getDayOfWeekEnum())));
        }
        workScheduleRepository.save(workSchedule);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject(HttpStatus.OK.toString(),
                        "Get work schedules successful!", null));
    }

    @Override
    public ResponseEntity<ResponseObject> deleteWorkSchedule(int userId, int workScheduleId) {
        try {
            WorkSchedule workSchedule = findById(workScheduleId);
            isPermission(userId, workSchedule);
            workScheduleRepository.delete(workSchedule);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete a work schedule successful!", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }
    private WorkSchedule findById(int id) {
        return workScheduleRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Work schedule is not exist!"));
    }

    private void isPermission(Integer userId, WorkSchedule workSchedule) throws UserNotHavePermissionException {
        if (!Objects.equals(workSchedule.getHelperInformation().getUser().getUserId(), userId)) {
            throw new UserNotHavePermissionException("User do not have permission to do this action!");
        }
    }

    private List<GetWorkScheduleResponse> mappingFromDomain(List<WorkSchedule> workSchedules) {
        Map<DayOfWeekEnum, List<TimeRangeOfDay>> customWorkSchedulesMap = new HashMap<>();
        for (WorkSchedule schedule : workSchedules) {
            DayOfWeekEnum dayOfWeek = schedule.getDayOfWeek();
            TimeRangeOfDay timeRangeOfDay = new TimeRangeOfDay(schedule.getWorkScheduleId(), schedule.getStartTime(), schedule.getEndTime());

            if (!customWorkSchedulesMap.containsKey(dayOfWeek)) {
                customWorkSchedulesMap.put(dayOfWeek, new ArrayList<>());
            }
            customWorkSchedulesMap.get(dayOfWeek).add(timeRangeOfDay);
        }
        List<GetWorkScheduleResponse> customWorkSchedules = new ArrayList<>();
        for (Map.Entry<DayOfWeekEnum, List<TimeRangeOfDay>> entry : customWorkSchedulesMap.entrySet()) {
            GetWorkScheduleResponse customSchedule = new GetWorkScheduleResponse();
            customSchedule.setDayOfWeekEnum(entry.getKey());
            customSchedule.setTimes(entry.getValue());
            customWorkSchedules.add(customSchedule);
        }
        return customWorkSchedules;
    }
}