package iclean.code.function.jobunit.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.jobunit.CreateJobUnitRequest;
import iclean.code.data.dto.request.jobunit.UpdateJobUnitRequest;
import iclean.code.function.jobunit.service.JobUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@Tag(name = "Job Unit API")
@RequestMapping("api/v1/job-unit")
@Validated
public class JobUnitController {
    @Autowired
    private JobUnitService jobUnitService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Get all job units are not deleted", description = "Return all job units information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Units Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getJobUnitActives() {
        return jobUnitService.getJobUnitActives();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Get all job units", description = "Return all job units information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Units Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getJobUnits() {
        return jobUnitService.getJobUnits();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Create new job unit", description = "Return all job units information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create job unit successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createJobs(@ModelAttribute @Valid CreateJobUnitRequest request) {
        return jobUnitService.createJobUnits(request);
    }

    @PutMapping(value = "{jobUnitId}")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Update a job unit", description = "Return all job units information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update job unit successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateJobUnit(@PathVariable("jobUnitId") int jobUnitId,
                                                    @ModelAttribute @Valid UpdateJobUnitRequest request) {
        return jobUnitService.updateJobUnit(jobUnitId, request);
    }

    @DeleteMapping(value = "{jobUnitId}")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Delete a job", description = "Return all job units information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update job successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> deleteJobUnit(@PathVariable("jobUnitId") int jobUnitId) {
        return jobUnitService.deleteJobUnit(jobUnitId);
    }
}
