package iclean.code.function.jobunit.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.jobunit.CreateJobUnitRequest;
import iclean.code.data.dto.request.jobunit.UpdateJobUnitRequest;
import iclean.code.function.jobunit.service.JobUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;

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
    public ResponseEntity<ResponseObject> createJobs(@NotNull(message = "Price Default is required")
                                                     @Schema(example = "10000")
                                                     @Min(value = 20000, message = "Price Default cannot be smaller than 20000")
                                                     @Max(value = 500000, message = "Price Default cannot be greater than 500000")
                                                     @RequestPart Double priceDefault,
                                                     @NotNull(message = "Employee Commission is required")
                                                     @Schema(example = "30")
                                                     @Min(value = 10, message = "Employee Commission cannot be smaller than 10")
                                                     @Max(value = 70, message = "Employee Commission cannot be greater than 70")
                                                     @RequestPart Double employeeCommission,
                                                     @NotNull(message = "File is required")
                                                     @RequestPart MultipartFile imgUnitFile,
                                                     @NotNull(message = "Unit Value cannot be null")
                                                     @NotBlank(message = "Unit Value is empty")
                                                     @Pattern(regexp = "^[0-9a-zA-Z/]+$", message = "Unit Value is invalid")
                                                     @Length(max = 200, message = "Max length: 200")
                                                     @RequestPart String unitValue) {
        return jobUnitService.createJobUnits(new CreateJobUnitRequest(priceDefault, employeeCommission, imgUnitFile, unitValue));
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
    public ResponseEntity<ResponseObject> updateJobUnit(@PathVariable("jobUnitId") Integer jobUnitId,
                                                        @NotNull(message = "Price Default is required")
                                                        @Schema(example = "10000")
                                                        @Min(value = 20000, message = "Price Default cannot be smaller than 20000")
                                                        @Max(value = 500000, message = "Price Default cannot be greater than 500000")
                                                        @RequestPart Double priceDefault,
                                                        @NotNull(message = "Employee Commission is required")
                                                        @Schema(example = "30")
                                                        @Min(value = 10, message = "Employee Commission cannot be smaller than 10")
                                                        @Max(value = 70, message = "Employee Commission cannot be greater than 70")
                                                        @RequestPart Double employeeCommission,
                                                        @NotNull(message = "File is required")
                                                        @RequestPart MultipartFile imgUnitFile,
                                                        @NotNull(message = "Unit Value cannot be null")
                                                        @NotBlank(message = "Unit Value is empty")
                                                        @Pattern(regexp = "^[0-9a-zA-Z/]+$", message = "Unit Value is invalid")
                                                        @Length(max = 200, message = "Max length: 200")
                                                        @RequestPart String unitValue) {
        return jobUnitService.updateJobUnit(jobUnitId, new UpdateJobUnitRequest(priceDefault, employeeCommission, imgUnitFile, unitValue));
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
