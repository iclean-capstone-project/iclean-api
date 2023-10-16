package iclean.code.function.reporttype.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.reporttype.AddReportTypeRequest;
import iclean.code.data.dto.request.reporttype.UpdateReportTypeRequest;
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
@RequestMapping("api/v1/reportType")
@Tag(name = "Report Type")
public class ReportTypeController {
    @Autowired
    private ReportTypeService reportTypeService;

    @GetMapping
    @Operation(summary = "User Get All Report Type", description = "Return User Get All Report Type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Get All Report Type success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getAllReportType() {
        return reportTypeService.getAllReportType();
    }

    @GetMapping(value = "{reportTypeId}")
    @Operation(summary = "User Get All Report Type By Id", description = "Return User Get All Report Type By Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Get All Report Type success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getReportTypeById(@PathVariable("reportTypeId") @Valid int reportTypeId) {
        return reportTypeService.getReportTypeById(reportTypeId);
    }

    @PostMapping
    @Operation(summary = "Add Report Type", description = "Return success or fail message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add Report Type success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> addReportType(@RequestBody @Valid AddReportTypeRequest request) {
        return reportTypeService.addReportType(request);
    }

    @PutMapping(value = "{reportTypeId}")
    @Operation(summary = "Update Report Type", description = "Return User Get All Report Type By Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update Report Type success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> updateReportType(@PathVariable("reportTypeId") int reportTypeId,
                                                              @RequestBody @Valid UpdateReportTypeRequest request) {
        return reportTypeService.updateReportType(reportTypeId, request);
    }

    @DeleteMapping(value = "{reportTypeId}")
    @Operation(summary = "Delete Report Type", description = "Return User Get All Report Type By Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete Report Type success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> deleteReportType(@PathVariable("reportTypeId") @Valid int reportTypeId) {
        return reportTypeService.deleteReportType(reportTypeId);
    }
}
