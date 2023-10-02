package iclean.code.function.address.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.address.CreateAddressRequestDTO;
import iclean.code.data.dto.request.address.GetAddressRequestDTO;
import iclean.code.data.dto.request.address.UpdateAddressRequestDTO;
import iclean.code.function.address.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/address")
@Tag(name = "Address API")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @GetMapping
    @Operation(summary = "Get all addresses of a user", description = "Return all addresses information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Addresses Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getAddresses(@RequestParam @Valid GetAddressRequestDTO request) {
        return addressService.getAddresses(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a address of a user by id", description = "Return address information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getAddress(@PathVariable Integer id) {
        return addressService.getAddress(id);
    }

    @PostMapping
    @Operation(summary = "Create new address of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new Address Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createAddress(@RequestBody @Valid CreateAddressRequestDTO request) {
        return addressService.createAddress(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a address of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a Address Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateAddress(@RequestBody @Valid UpdateAddressRequestDTO request,
                                                        @PathVariable Integer id) {
        return addressService.updateAddress(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a address of a user by id", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete a Address Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateAddress(@PathVariable Integer id) {
        return addressService.deleteAddress(id);
    }

}
