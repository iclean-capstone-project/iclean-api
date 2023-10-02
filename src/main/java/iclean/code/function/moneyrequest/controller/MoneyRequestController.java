package iclean.code.function.moneyrequest.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.moneyrequest.CreateMoneyRequestRequestDTO;
import iclean.code.data.dto.request.moneyrequest.GetMoneyRequestRequestDTO;
import iclean.code.data.dto.request.moneyrequest.UpdateMoneyRequestRequestDTO;
import iclean.code.function.moneyrequest.service.MoneyRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/money-request")
@Tag(name = "MoneyRequest API")
public class MoneyRequestController {
    @Autowired
    private MoneyRequestService moneyRequestService;

    @GetMapping
    @Operation(summary = "Get all money requests of a user", description = "Return all money requests information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Money Requests Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getMoneyRequests(@RequestParam @Valid GetMoneyRequestRequestDTO request) {
        return moneyRequestService.getMoneyRequests(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a money request of a user by id", description = "Return money request information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Money Request Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getMoneyRequest(@PathVariable Integer id) {
        return moneyRequestService.getMoneyRequest(id);
    }

    @PostMapping
    @Operation(summary = "Create new address of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new Money Request Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createMoneyRequest(@RequestBody @Valid CreateMoneyRequestRequestDTO request) {
        return moneyRequestService.createMoneyRequest(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a address of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a Money Request Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateMoneyRequest(@RequestBody @Valid UpdateMoneyRequestRequestDTO request,
                                                        @PathVariable Integer id) {
        return moneyRequestService.updateMoneyRequest(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a address of a user by id", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete a Money Request Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateMoneyRequest(@PathVariable Integer id) {
        return moneyRequestService.deleteMoneyRequest(id);
    }

}
