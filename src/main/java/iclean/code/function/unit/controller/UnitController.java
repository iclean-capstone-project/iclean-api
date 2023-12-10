package iclean.code.function.unit.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.function.unit.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
}
