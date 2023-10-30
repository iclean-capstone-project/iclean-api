package iclean.code.function.service.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.service.CreateServiceRequest;
import iclean.code.data.dto.request.service.UpdateServiceRequest;
import iclean.code.function.service.service.JobService;
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
@Tag(name = "Service API")
@RequestMapping("api/v1/service")
@Tag(name = "Service")
public class ServiceController {
    @Autowired
    private JobService jobService;

    @GetMapping
    @Operation(summary = "Get all service", description = "Return all service information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getAllJob() {
        return jobService.getJobs();
    }

    @PostMapping
    @Operation(summary = "Create Service ", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create service success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    public ResponseEntity<ResponseObject> addJob(@RequestBody @Valid CreateServiceRequest request) {
        return jobService.createJob(request);
    }

    @PutMapping(value = "{jobId}")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Update a service", description = "Return all jobs information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update service successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateJob(@PathVariable("jobId") int jobId,
                                                    @ModelAttribute @Valid UpdateServiceRequest request) {
        return jobService.updateJob(jobId, request);
    }

    @DeleteMapping(value = "{jobId}")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Delete a service", description = "Return all jobs information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update service successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> deleteJob(@PathVariable("jobId") int jobId) {
        return jobService.deleteJob(jobId);
    }
}
