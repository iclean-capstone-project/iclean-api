package iclean.code.function.serviceprice.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.serviceprice.GetServicePriceRequest;
import iclean.code.data.dto.request.serviceprice.ServicePriceRequest;
import iclean.code.function.serviceprice.service.ServicePriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/service-price")
@Tag(name = "Service Price Api")
@Validated
public class ServicePriceController {
    @Autowired
    private ServicePriceService servicePriceService;

    @GetMapping("/{serviceUnitId}")
    @Operation(summary = "Get Service Price", description = "Return Service Price detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return Service Price detail"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    public ResponseEntity<ResponseObject> getServicePriceActive(@PathVariable Integer serviceUnitId,
                                                                @RequestParam String startTime) {
        return servicePriceService.getServicePriceActive(new GetServicePriceRequest(serviceUnitId, startTime));
    }

    @PostMapping("/{serviceUnitId}")
    @Operation(summary = "Get Service Price", description = "Return Service Price detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return Service Price detail"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> createNewServicePrices(@RequestBody List<ServicePriceRequest> requests, @PathVariable Integer serviceUnitId) {
        return servicePriceService.createServicePrice(requests, serviceUnitId);
    }

    @GetMapping("detail/{serviceUnitId}")
    @Operation(summary = "Get Service Price", description = "Return Service Price detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return Service Price detail"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> getServicePrice(@PathVariable Integer serviceUnitId) {
        return servicePriceService.getServicePrice(serviceUnitId);
    }
}
