package iclean.code.function.serviceregistration.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.serviceregistration.UpdateStatusServiceRegistrationRequest;
import iclean.code.function.serviceregistration.service.ServiceRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/service-registration")
@Tag(name = "Service Registration API")
@Validated
public class ServiceRegistrationController {
    @Autowired
    private ServiceRegistrationService serviceRegistrationService;
    @PutMapping("/helper-service/{id}")
    @Operation(summary = "Turn on or off a service that helper want to update", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return message successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('employee')")
    public ResponseEntity<ResponseObject> updateServiceByHelper(@PathVariable Integer id,
                                                                @RequestBody @Valid UpdateStatusServiceRegistrationRequest request,
                                                                Authentication authentication) {
        return serviceRegistrationService.updateServiceByHelper(JwtUtils.decodeToAccountId(authentication), id, request);
    }

    @GetMapping
    @Operation(summary = "Get services that of a helper", description = "Return list service that helper can do")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return list service that helper can do"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('employee')")
    public ResponseEntity<ResponseObject> getServiceRegistration(Authentication authentication) {
        return serviceRegistrationService.getServiceRegistrationActive(JwtUtils.decodeToAccountId(authentication));
    }

}
