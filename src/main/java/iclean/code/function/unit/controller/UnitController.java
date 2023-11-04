package iclean.code.function.unit.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.unit.CreateUnitRequest;
import iclean.code.data.dto.request.unit.UpdateUnitRequest;
import iclean.code.function.unit.service.UnitService;
import iclean.code.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;

@RestController
@Tag(name = "Unit API")
@RequestMapping("api/v1/unit")
@Validated
public class UnitController {
    @Autowired
    private UnitService unitService;
    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Get all job units", description = "Return all job units information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Units Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getUnits() {
        return unitService.getUnits();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Create new unit", description = "Return status success or fail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create unit successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createUnit(@NotNull(message = "Unit Detail is required")
                                                     @NotBlank(message = "Unit Detail cannot be empty")
                                                     @Schema(example = "10 m2")
                                                     @RequestPart String unitDetail,
                                                     @NotNull(message = "File is required")
                                                     @RequestPart MultipartFile imgUnitFile,
                                                     @NotNull(message = "Unit Value cannot be null")
                                                     @Pattern(regexp = "\\d+\\.?\\d*",message = "Unit Value is required number")
                                                     @Schema(example = "1")
                                                     @Range(min = 1, max = 10, message = "Unit value cannot be greater than 10 and smaller than 1")
                                                     @RequestPart String unitValue) {
        return unitService.createUnit(new CreateUnitRequest(unitDetail, Double.parseDouble(unitValue), imgUnitFile));
    }

    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Update a job unit", description = "Return all job units information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update job unit successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateJobUnit(@PathVariable("id") Integer id,
                                                        @Schema(example = "10 m2")
                                                        @RequestPart(required = false) String unitDetail,
                                                        @RequestPart(required = false) MultipartFile imgUnitFile,
                                                        @Schema(example = "1")
                                                        @Pattern(regexp = "\\d+\\.?\\d*",message = "Unit Value is required number")
                                                        @RequestPart(required = false) String unitValue,
                                                        @Schema(example = "Active")
                                                        @Pattern(regexp = "(?i)(Active)", message = "Unit Status are invalid")
                                                        @RequestPart(required = false) String unitStatus) {
        return unitService.updateUnit(id, new UpdateUnitRequest(unitDetail,
                !Utils.isNullOrEmpty(unitValue) ? Double.parseDouble(unitValue) : null,
                unitStatus, imgUnitFile));
    }

    @DeleteMapping(value = "{id}")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Delete a unit", description = "Return message success or fail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete unit successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> deleteJobUnit(@PathVariable("id") int id) {
        return unitService.deleteUnit(id);
    }
}
