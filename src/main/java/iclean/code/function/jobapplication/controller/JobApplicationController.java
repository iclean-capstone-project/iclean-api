package iclean.code.function.jobapplication.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.jobapplication.CreateJobApplicationRequestDTO;
import iclean.code.data.dto.request.jobapplication.GetJobApplicationRequestDTO;
import iclean.code.data.dto.request.jobapplication.UpdateJobApplicationRequestDTO;
import iclean.code.function.jobapplication.service.JobApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/job-application")
@Tag(name = "Job Application API")
public class JobApplicationController {
    @Autowired
    private JobApplicationService jobApplicationService;

    @GetMapping
    @Operation(summary = "Get all job application of a employee", description = "Return all job application information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job application Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getJobApplications(@RequestParam @Valid GetJobApplicationRequestDTO request) {
        return jobApplicationService.getJobApplications(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a job application of a employee by id", description = "Return job application information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job application Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getJobApplication(@PathVariable Integer id) {
        return jobApplicationService.getJobApplication(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create new job application of a employee", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new job application Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createJobApplication(@RequestBody @Valid CreateJobApplicationRequestDTO request,
                                                               @RequestPart MultipartFile file) {
        return jobApplicationService.createJobApplication(request, file);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job application of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a job application Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateJobApplication(@RequestBody @Valid UpdateJobApplicationRequestDTO request,
                                                               @PathVariable Integer id,
                                                               @RequestPart MultipartFile file) {
        return jobApplicationService.updateJobApplication(id, request, file);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job application of a employee by id", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete a job application Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> deleteJobApplication(@PathVariable Integer id) {
        return jobApplicationService.deleteJobApplication(id);
    }
}
