package iclean.code.function.dashboard.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.function.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import java.time.LocalTime;

@RestController
@RequestMapping("api/v1/dashboard")
@Tag(name = "Dashboard API")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/home")
    @Operation(summary = "Count all user of system", description = "Return sum of user in system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tổng người dùng "),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    public ResponseEntity<ResponseObject> homeDashboard() {
        return dashboardService.homeDashboard();
    }


    @GetMapping("/get-booking-at-date")
    @Operation(summary = "Count all booking of system", description = "Return sum of booking in system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tổng đơn hàng "),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    public ResponseEntity<ResponseObject> findBookingAtDate(@RequestParam @Nullable String time,
                                                            @RequestParam @Nullable String option) {
        return dashboardService.findBookingByDate(time, option);
    }

    @GetMapping("/count-booking-per-day")
    @Operation(summary = "Count and get Sale of booking per day", description = "Return sum of booking in system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tổng đơn hàng "),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    public ResponseEntity<ResponseObject> getSumOfBookingPerDay(@RequestParam Integer month,
                                                            @RequestParam Integer year) {
        return dashboardService.getSumOfBookingPerDay(month, year);
    }
}
