package iclean.code.function.report.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.report.CreateReportRequest;
import iclean.code.data.dto.request.report.UpdateReportRequest;
import iclean.code.function.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("api/v1/report")
@Tag(name = "Report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    @Operation(summary = "User Get All Report", description = "Return User Get All Report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Get All Report success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('manager')")
    public ResponseEntity<ResponseObject> getReports(
            @RequestParam(name = "renterName", defaultValue = "") String renterName,
            @RequestParam(name = "displayAll", defaultValue = "false") Boolean displayAll,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);
        return reportService.getReports(JwtUtils.decodeToAccountId(authentication), renterName, displayAll, pageable);
    }

    @GetMapping(value = "{reportId}")
    @Operation(summary = "Get Report by ID", description = "Return Report by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Report by ID success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'manager')")
    public ResponseEntity<ResponseObject> getReportById(
            @PathVariable("reportId") @Valid Integer reportId) {
        return reportService.getReportById(reportId);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add Report", description = "Return fail or success message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new Report"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter')")
    public ResponseEntity<ResponseObject> createReport(@RequestPart(name = "bookingId")
                                                       @Min(value = 1, message = "Booking ID cannot smaller than 1")
                                                       Integer bookingId,
                                                       @RequestPart(name = "reportTypeId")
                                                       @Min(value = 1, message = "Report Type ID cannot smaller than 1")
                                                       Integer reportTypeId,
                                                       @Length(max = 200, message = "Detail length cannot greater than 200")
                                                       @NotNull(message = "Detail cannot be null")
                                                       @NotBlank(message = "Detail cannot be empty")
                                                       String detail,
                                                       @RequestPart(name = "files", required = false)
                                                       List<MultipartFile> files, Authentication authentication) {
        return reportService.createReport(new CreateReportRequest(bookingId, reportTypeId, detail, files), JwtUtils.decodeToAccountId(authentication));
    }

    @PutMapping(value = "{reportId}")
    @Operation(summary = "Update Report", description = "Return fail or success message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update Report"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> updateReport(
            @PathVariable("reportId") int reportId,
            @RequestBody @Valid UpdateReportRequest request) {
        return reportService.updateReport(reportId, request);
    }

    @DeleteMapping(value = "{reportId}")
    @Operation(summary = "Reject a Report by manager", description = "Return fail or success message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete Report"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('manager')")
    public ResponseEntity<ResponseObject> deleteReport(@PathVariable("reportId") @Valid int reportId) {
        return reportService.deleteReport(reportId);
    }
}
