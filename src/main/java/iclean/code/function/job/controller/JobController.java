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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/job")
@Tag(name = "Job")
public class JobController {
    @Autowired
    private JobService jobService;

    @GetMapping
    @Operation(summary = "Get all job", description = "Return all job information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
//            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getAllJob() {
        return jobService.getAllJob();
    }

    @PostMapping
    @Operation(summary = "Create Job ", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create job success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    public ResponseEntity<ResponseObject> addJob(@RequestBody @Valid AddJobRequest request) {
        return jobService.addJob(request);
    }

    @PutMapping(value = "{jobId}")
    public ResponseEntity<ResponseObject> updateJob(@PathVariable("jobId") int jobId,
                                                    @RequestBody @Valid UpdateJobRequest request) {
        return jobService.updateJob(jobId, request);
    }

    @DeleteMapping(value = "{jobId}")
    public ResponseEntity<ResponseObject> deleteJob(@PathVariable("jobId") int jobId) {
        return jobService.deleteJob(jobId);
    }
}
