package iclean.code.function.wallethistory.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.wallethistory.CreateWalletHistoryRequestDTO;
import iclean.code.data.dto.request.wallethistory.UpdateWalletHistoryRequestDTO;
import iclean.code.function.wallethistory.service.WalletHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/wallet-history")
@Tag(name = "Wallet History API")
public class WalletHistoryController {
    @Autowired
    private WalletHistoryService walletHistoryService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Get all wallet histories of a user", description = "Return all wallet histories information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet Histories Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getWalletHistories(Authentication authentication) {
        return walletHistoryService.getWalletHistories(JwtUtils.decodeToAccountId(authentication));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Get a address of a user by id", description = "Return address information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "WalletHistory Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getWalletHistory(@PathVariable Integer id,
                                                           Authentication authentication) {
        return walletHistoryService.getWalletHistory(id, JwtUtils.decodeToAccountId(authentication));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Create new address of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new WalletHistory Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createWalletHistory(@RequestBody @Valid CreateWalletHistoryRequestDTO request,
                                                              Authentication authentication) {
        return walletHistoryService.createWalletHistory(request, JwtUtils.decodeToAccountId(authentication));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Update a address of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a WalletHistory Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateWalletHistory(@RequestBody @Valid UpdateWalletHistoryRequestDTO request,
                                                              @PathVariable Integer id,
                                                              Authentication authentication) {
        return walletHistoryService.updateWalletHistory(id, request, JwtUtils.decodeToAccountId(authentication));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Delete a address of a user by id", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete a WalletHistory Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateWalletHistory(@PathVariable Integer id,
                                                              Authentication authentication) {
        return walletHistoryService.deleteWalletHistory(id, JwtUtils.decodeToAccountId(authentication));
    }

}
