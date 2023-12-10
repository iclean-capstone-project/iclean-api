package iclean.code.function.serviceunit.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.common.SortRequestBuilder;
import iclean.code.data.dto.request.serviceunit.CreateServiceUnitRequest;
import iclean.code.data.dto.request.serviceunit.UpdateServiceUnitRequest;
import iclean.code.data.dto.response.serviceunit.GetServiceUnitResponse;
import iclean.code.function.serviceunit.service.ServiceUnitService;
import iclean.code.utils.validator.ValidSortFields;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/service-unit")
@Tag(name = "Service Unit API")
@Validated
public class ServiceUnitController {
    @Autowired
    private ServiceUnitService serviceUnitService;

    @GetMapping("/active")
    @PreAuthorize("hasAnyAuthority('renter')")
    @Operation(summary = "Get all service unit for renter", description = "Return all service unit information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service Unit Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getServiceUnitsForRenter(@RequestParam Integer serviceId) {
        return serviceUnitService.getServiceUnitsForRenter(serviceId);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @Operation(summary = "Get all service unit for admin and manager", description = "Return all service unit information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service Unit Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getServiceUnits(@RequestParam Integer serviceId,
                                                          @RequestParam(name = "sort", required = false)
                                                          @ValidSortFields(value = GetServiceUnitResponse.class) List<String> sortFields) {
        Sort sort = SortRequestBuilder.buildSortRequest();
        if (sortFields != null && !sortFields.isEmpty()) {
            sort = SortRequestBuilder.buildSortRequest(sortFields);
        }
        return serviceUnitService.getServiceUnits(serviceId, sort);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @Operation(summary = "Get all service unit for admin and manager", description = "Return all service unit information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service Unit Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getServiceUnits(@PathVariable Integer id) {
        return serviceUnitService.getServiceUnit(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('admin')")
    @Operation(summary = "Create new service unit", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new service unit successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createServiceUnit(@RequestBody @Valid CreateServiceUnitRequest request) {
        return serviceUnitService.createServiceUnit(request);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('admin')")
    @Operation(summary = "Update a service unit", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a service unit successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateServiceUnit(@RequestBody @Valid UpdateServiceUnitRequest request,
                                                            @PathVariable Integer id) {
        return serviceUnitService.updateServiceUnit(id, request);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('admin')")
    @Operation(summary = "Delete a service unit", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a service unit successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> deleteServiceUnit(@PathVariable Integer id) {
        return serviceUnitService.deleteServiceUnit(id);
    }
}
