package iclean.code.function.workschedule.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.workschedule.CreateWorkScheduleRequest;
import iclean.code.function.workschedule.service.WorkScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Tag(name = "Work Schedule API")
@RequestMapping("api/v1/work-schedule")
@Validated
public class WorkScheduleController {
    @Autowired
    private WorkScheduleService workScheduleService;
    @GetMapping
    @PreAuthorize("hasAuthority('employee')")
    @Operation(summary = "Get helper work schedule by helper", description = "Return all helper work schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Helper work schedules Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getWorkSchedules(Authentication authentication) {
        return workScheduleService.getWorkSchedules(JwtUtils.decodeToAccountId(authentication));
    }

    @GetMapping("{helperId}")
    @PreAuthorize("hasAuthority('manager')")
    @Operation(summary = "Get helper work schedule by manager", description = "Return all helper work schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Helper work schedules Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getWorkScheduleOfHelpers(@PathVariable Integer helperId) {
        return workScheduleService.getWorkSchedules(helperId);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('employee')")
    @Operation(summary = "Update a work schedule", description = "Return status success or fail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update work schedule successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateWorkSchedule(@RequestBody @Valid List<CreateWorkScheduleRequest> requests,
                                                        Authentication authentication) {
        return workScheduleService.updateWorkSchedule(JwtUtils.decodeToAccountId(authentication), requests);
    }
}
