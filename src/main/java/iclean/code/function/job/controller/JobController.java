package iclean.code.function.job.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.job.CreateJobRequest;
import iclean.code.data.dto.request.job.UpdateJobRequest;
import iclean.code.function.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Tag(name = "Job API")
@RequestMapping("api/v1/job")
public class JobController {
    @Autowired
    private JobService jobService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Get all jobs are not deleted", description = "Return all jobs information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jobs Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getJobActives() {
        return jobService.getJobActives();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Get all jobs", description = "Return all jobs information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jobs Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getJobs() {
        return jobService.getJobs();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Create new job", description = "Return all jobs information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create job successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createJob(@ModelAttribute @Valid CreateJobRequest request) {
        return jobService.createJob(request);
    }

    @PutMapping(value = "{jobId}")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Update a job", description = "Return all jobs information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update job successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateJob(@PathVariable("jobId") int jobId,
                                                    @ModelAttribute @Valid UpdateJobRequest request) {
        return jobService.updateJob(jobId, request);
    }

    @DeleteMapping(value = "{jobId}")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Delete a job", description = "Return all jobs information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update job successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> deleteJob(@PathVariable("jobId") int jobId) {
        return jobService.deleteJob(jobId);
    }
}
