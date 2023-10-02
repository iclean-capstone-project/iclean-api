package iclean.code.function.wallethistory.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.wallethistory.CreateWalletHistoryRequestDTO;
import iclean.code.data.dto.request.wallethistory.GetWalletHistoryRequestDTO;
import iclean.code.data.dto.request.wallethistory.UpdateWalletHistoryRequestDTO;
import iclean.code.function.wallethistory.service.WalletHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/wallet-history")
@Tag(name = "Wallet History API")
public class WalletHistoryController {
    @Autowired
    private WalletHistoryService walletHistoryService;

    @GetMapping
    @Operation(summary = "Get all wallet histories of a user", description = "Return all wallet histories information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet Histories Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getWalletHistories(@RequestParam @Valid GetWalletHistoryRequestDTO request) {
        return walletHistoryService.getWalletHistories(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a address of a user by id", description = "Return address information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "WalletHistory Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getWalletHistory(@PathVariable Integer id) {
        return walletHistoryService.getWalletHistory(id);
    }

    @PostMapping
    @Operation(summary = "Create new address of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new WalletHistory Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createWalletHistory(@RequestBody @Valid CreateWalletHistoryRequestDTO request) {
        return walletHistoryService.createWalletHistory(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a address of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a WalletHistory Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateWalletHistory(@RequestBody @Valid UpdateWalletHistoryRequestDTO request,
                                                        @PathVariable Integer id) {
        return walletHistoryService.updateWalletHistory(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a address of a user by id", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete a WalletHistory Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateWalletHistory(@PathVariable Integer id) {
        return walletHistoryService.deleteWalletHistory(id);
    }

}
