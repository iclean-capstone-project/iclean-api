package iclean.code.function.report.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.report.AddReportRequest;
import iclean.code.data.dto.request.report.UpdateReportRequest;
import iclean.code.data.dto.response.address.GetAddressResponseDto;
import iclean.code.function.report.service.ReportService;
import iclean.code.utils.validator.ValidSortFields;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/report")
@Tag(name = "Report")
public class ReportController {

    @Autowired
    private ReportService reportService;

//    @GetMapping
//    @Operation(summary = "Get All Report", description = "Return All Report")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Get All Report success"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
//            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
//            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
//    })
//    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
//    public ResponseEntity<ResponseObject> getAllReport(
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            @RequestParam(name = "size", defaultValue = "10") int size,
//            @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetAddressResponseDto.class) List<String> sortFields)
//    {
//        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);
//        if (sortFields != null && !sortFields.isEmpty()) {
//            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
//        }
//        return reportService.getAllReportAsAdminOrManager(pageable);
//    }

    @GetMapping("/renter")
    @Operation(summary = "User Get All Report", description = "Return User Get All Report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Get All Report success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getAllReportAsRenter(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false)  List<String> sortFields,
            Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);
        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
        }
        return reportService.getAllReport(JwtUtils.decodeToAccountId(authentication), pageable);
    }

    @GetMapping(value = "{reportId}")
    @Operation(summary = "Get Report by ID", description = "Return Report by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Report by ID success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getReportById(
            @PathVariable("reportId") @Valid Integer reportId,
            Authentication authentication) {
        return reportService.getReportById(reportId, JwtUtils.decodeToAccountId(authentication));
    }

    @PostMapping
    @Operation(summary = "Add Report", description = "Return fail or success message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new Report"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> addReport(@RequestBody @Valid AddReportRequest request) {
        return reportService.addReport(request);
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
    @Operation(summary = "Delete Report", description = "Return fail or success message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete Report"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> deleteReport(@PathVariable("reportId") @Valid int reportId) {
        return reportService.deleteReport(reportId);
    }
}
