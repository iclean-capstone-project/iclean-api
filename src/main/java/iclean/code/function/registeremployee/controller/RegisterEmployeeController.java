package iclean.code.function.registeremployee.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.registeremployee.CreateRegisterEmployeeRequestDTO;
import iclean.code.data.dto.request.registeremployee.GetRegisterEmployeeRequestDTO;
import iclean.code.data.dto.request.registeremployee.UpdateRegisterEmployeeRequestDTO;
import iclean.code.function.registeremployee.service.RegisterEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/new-employee")
@Tag(name = "New Employee API")
public class RegisterEmployeeController {
    @Autowired
    private RegisterEmployeeService registerEmployeeService;

    @GetMapping
    @Operation(summary = "Get all new employees of a user", description = "Return all new employees information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New Employees Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getRegisterEmployees(@RequestParam @Valid GetRegisterEmployeeRequestDTO request) {
        return registerEmployeeService.getRegisterEmployees(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a address of a user by id", description = "Return address information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "RegisterEmployee Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getRegisterEmployee(@PathVariable Integer id) {
        return registerEmployeeService.getRegisterEmployee(id);
    }

    @PostMapping
    @Operation(summary = "Create new address of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new RegisterEmployee Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createRegisterEmployee(@RequestBody @Valid CreateRegisterEmployeeRequestDTO request) {
        return registerEmployeeService.createRegisterEmployee(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a address of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a RegisterEmployee Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateRegisterEmployee(@RequestBody @Valid UpdateRegisterEmployeeRequestDTO request,
                                                        @PathVariable Integer id) {
        return registerEmployeeService.updateRegisterEmployee(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a address of a user by id", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete a RegisterEmployee Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateRegisterEmployee(@PathVariable Integer id) {
        return registerEmployeeService.deleteRegisterEmployee(id);
    }

}
