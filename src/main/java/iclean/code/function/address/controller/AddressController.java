package iclean.code.function.address.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.address.CreateAddressRequestDTO;
import iclean.code.data.dto.request.address.UpdateAddressRequestDTO;
import iclean.code.data.dto.response.address.GetAddressResponseDto;
import iclean.code.function.address.service.AddressService;
import iclean.code.utils.validator.ValidSortFields;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
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
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    public ResponseEntity<ResponseObject> getAddresses(
            @RequestParam(name = "search", defaultValue = "ví dụ", required = false) String search,
            @RequestParam(name = "page", defaultValue = "1", required = false) @Min(value = 1, message = "Page cannot be smaller 1") Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(value = 1, message = "Size cannot be smaller 1") Integer size,
            @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetAddressResponseDto.class) List<String> sortFields,
            Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);

        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
        }
        return addressService.getAddresses(JwtUtils.decodeToAccountId(authentication), pageable, search);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Get a address of a user by id", description = "Return address information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getAddress(@PathVariable Integer id, Authentication authentication) {
        return addressService.getAddress(id, JwtUtils.decodeToAccountId(authentication));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Create new address of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new Address Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createAddress(@RequestBody @Valid CreateAddressRequestDTO request,
                                                        Authentication authentication) {
        return addressService.createAddress(request, JwtUtils.decodeToAccountId(authentication));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Update a address of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a Address Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateAddress(@RequestBody @Valid UpdateAddressRequestDTO request,
                                                        @PathVariable Integer id,
                                                        Authentication authentication) {
        return addressService.updateAddress(id, request, JwtUtils.decodeToAccountId(authentication));
    }

    @PutMapping("/status/{id}")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Update a address of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a Address Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateDefaultAddress(@PathVariable Integer id,
                                                        Authentication authentication) {
        return addressService.setDefaultAddress(id, JwtUtils.decodeToAccountId(authentication));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Delete a address of a user by id", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete a Address Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateAddress(@PathVariable Integer id,
                                                        Authentication authentication) {
        return addressService.deleteAddress(id, JwtUtils.decodeToAccountId(authentication));
    }

}
