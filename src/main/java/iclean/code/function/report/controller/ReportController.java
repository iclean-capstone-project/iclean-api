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
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
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
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
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
    @PreAuthorize("hasAnyAuthority('renter', 'manager', 'admin')")
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
    @PreAuthorize("hasAnyAuthority('renter', 'helper')")
    public ResponseEntity<ResponseObject> createReport(@RequestPart(name = "bookingDetailId")
                                                       @Pattern(regexp = "^\\d+$", message = "Booking Detail ID is required number")
                                                       String bookingDetailId,
                                                       @RequestPart(name = "reportTypeId")
                                                       @Pattern(regexp = "^\\d+$", message = "Report Type ID is required number")
                                                       String reportTypeId,
                                                       @RequestPart
                                                       @Length(max = 200, message = "Detail length cannot greater than 200")
                                                       @NotNull(message = "Detail cannot be null")
                                                       @NotBlank(message = "Detail cannot be empty")
                                                       String detail,
                                                       @RequestPart(name = "image_1", required = false)
                                                       MultipartFile images_1,
                                                       @RequestPart(name = "image_2", required = false)
                                                       MultipartFile images_2,
                                                       @RequestPart(name = "image_3", required = false)
                                                       MultipartFile images_3, Authentication authentication) {
        List<MultipartFile> images = new ArrayList<>();
        if (images_1 != null) {
            images.add(images_1);
        }
        if (images_2 != null) {
            images.add(images_2);
        }
        if (images_3 != null) {
            images.add(images_3);
        }
        return reportService.createReport(new CreateReportRequest(Integer.parseInt(bookingDetailId),
                Integer.parseInt(reportTypeId), detail, images), JwtUtils.decodeToAccountId(authentication));
    }

    @PutMapping(value = "{reportId}")
    @Operation(summary = "Update Report", description = "Return fail or success message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update Report"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    public ResponseEntity<ResponseObject> updateReport(
            @PathVariable("reportId") int reportId,
            @RequestBody @Valid UpdateReportRequest request,
            Authentication authentication) {
        return reportService.updateReport(reportId, request, JwtUtils.decodeToAccountId(authentication));
    }

    @DeleteMapping(value = "{reportId}")
    @Operation(summary = "Reject a Report by manager", description = "Return fail or success message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete Report"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    public ResponseEntity<ResponseObject> deleteReport(@PathVariable("reportId") @Valid int reportId) {
        return reportService.deleteReport(reportId);
    }
}
