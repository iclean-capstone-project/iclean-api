package iclean.code.function.reporttype.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.reporttype.CreateReportType;
import iclean.code.function.reporttype.service.ReportTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/report-type")
@Tag(name = "Report Type")
public class ReportTypeController {
    @Autowired
    private ReportTypeService reportTypeService;
    @GetMapping
    @Operation(summary = "Get List Report Type", description = "Return List Report Type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get List Report Type"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'admin', 'manager')")
    public ResponseEntity<ResponseObject> getReportTypes() {
        return reportTypeService.getReportTypes();
    }

    @PostMapping
    @Operation(summary = "Create Report Type", description = "Return success or fail message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create Report Type success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ResponseObject> createReportType(@RequestBody @Valid CreateReportType request) {
        return reportTypeService.createReportType(request);
    }

    @DeleteMapping(value = "{reportTypeId}")
    @Operation(summary = "Delete Report Type", description = "Return User Get All Report Type By Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete Report Type success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ResponseObject> deleteReportType(@PathVariable("reportTypeId") @Valid int reportTypeId) {
        return reportTypeService.deleteReportType(reportTypeId);
    }
}
