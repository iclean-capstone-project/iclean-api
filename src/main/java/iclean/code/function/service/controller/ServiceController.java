package iclean.code.function.service.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.service.CreateServiceRequest;
import iclean.code.data.dto.request.service.UpdateServiceRequest;
import iclean.code.function.service.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.util.List;

@RestController
@Tag(name = "Service API")
@RequestMapping("api/v1/service")
@Validated
public class ServiceController {
    @Autowired
    private ServiceService serviceService;

    @GetMapping("/inactive")
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @Operation(summary = "Get all service", description = "Return all service information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getServices() {
        return serviceService.getServices();
    }

    @GetMapping("/detail")
    @PreAuthorize("hasAnyAuthority('employee')")
    @Operation(summary = "Get detail of a service for helper", description = "Return detail of a service information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service Detail Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getServiceUnitsForHelper(@RequestParam Integer serviceId) {
        return serviceService.getServiceForHelper(serviceId);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'renter', 'employee')")
    @Operation(summary = "Get service detail", description = "Return all service information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getService(@PathVariable Integer id) {
        return serviceService.getService(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'renter', 'employee')")
    @Operation(summary = "Get all service active for user and employee", description = "Return all service information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getServiceActives() {
        return serviceService.getServiceActives();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create Service ", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create service success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ResponseObject> createService(@NotNull(message = "Service name is required")
                                                        @Schema(example = "Quét nhà")
                                                        @NotBlank(message = "Service name cannot be empty")
                                                        @RequestPart String serviceName,
                                                        @NotNull(message = "Description of service is required")
                                                        @Schema(example = "Là dịch vụ dọn dẹp quét nhà")
                                                        @RequestPart String description,
                                                        @NotNull(message = "File is required")
                                                        @RequestPart MultipartFile serviceAvatar,
                                                        @RequestPart List<MultipartFile> serviceFileImages) {
        return serviceService.createService(new CreateServiceRequest(serviceName, description, serviceAvatar));
    }

    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Update a service", description = "Return all message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update service successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateService(@PathVariable("id") int serviceId,
                                                        @Schema(example = "Quét nhà")
                                                        @RequestPart(required = false) String serviceName,
                                                        @Schema(example = "Là dịch vụ dọn dẹp quét nhà")
                                                        @RequestPart(required = false) String description,
                                                        @Schema(example = "Active")
                                                        @Pattern(regexp = "(?i)(Active)", message = "Status are invalid")
                                                        @RequestPart(required = false) String serviceStatus,
                                                        @RequestPart(required = false) MultipartFile serviceAvatar,
                                                        @RequestPart(required = false) List<MultipartFile> serviceFileImages) {
        return serviceService.updateService(serviceId, new UpdateServiceRequest(serviceName, description, serviceStatus, serviceAvatar));
    }

    @DeleteMapping(value = "{id}")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Delete a service", description = "Return all message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update service successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> deleteService(@PathVariable("id") int id) {
        return serviceService.deleteService(id);
    }
}
