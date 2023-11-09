package iclean.code.function.attachment.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.attachment.CreateAttachmentRequestDTO;
import iclean.code.data.dto.request.attachment.UpdateAttachmentRequestDTO;
import iclean.code.function.attachment.service.JobApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/job-application")
@Tag(name = "Job Application API")
public class JobApplicationController {
    @Autowired
    private JobApplicationService jobApplicationService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @Operation(summary = "Get all job application", description = "Return all job application information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job application Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getJobApplications() {
        return jobApplicationService.getJobApplications();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('manager')")
    @Operation(summary = "Get a job application of a employee by employee id", description = "Return job application information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job application Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getJobApplication(@PathVariable Integer id) {
        return jobApplicationService.getJobApplication(id);
    }

    @GetMapping("/detail")
    @PreAuthorize("hasAuthority('employee')")
    @Operation(summary = "Get a job application of a employee", description = "Return job application information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job application Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getJobApplication(Authentication authentication) {
        return jobApplicationService.getJobApplication(JwtUtils.decodeToAccountId(authentication));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @Operation(summary = "Update a job application of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a job application Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateJobApplication(@RequestBody @Valid UpdateAttachmentRequestDTO request,
                                                               @PathVariable Integer id,
                                                               @RequestPart MultipartFile file) {
        return jobApplicationService.updateJobApplication(id, request, file);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin')")
    @Operation(summary = "Delete a job application of a employee by id", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete a job application Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> deleteJobApplication(@PathVariable Integer id) {
        return jobApplicationService.deleteJobApplication(id);
    }
}
