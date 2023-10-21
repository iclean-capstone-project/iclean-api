package iclean.code.function.wallet.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.wallet.UpdateBalance;
import iclean.code.function.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("api/v1/wallet")
@Tag(name = "Wallet Api")
@Validated
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping
    @Operation(summary = "Get current balance of wallet", description = "Return Get current balance of wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get current balance of wallet"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    public ResponseEntity<ResponseObject> getCurrentBalance(Authentication authentication,
                                                           @RequestParam(name = "type")
                                                           @Schema(example = "money|point")
                                                           @Pattern(regexp = "(?i)(money|point)", message = "Wallet Type is not valid")
                                                           @NotNull(message = "Wallet Type are invalid")
                                                           String walletType) {
        return walletService.getCurrentBalance(JwtUtils.decodeToAccountId(authentication), walletType);
    }

    @PutMapping(value = "{userId}")
    @Operation(summary = "Update Balance of a User", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update Balance success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    public ResponseEntity<ResponseObject> updateMoneyByUserId(@PathVariable("userId") Integer userId,
                                                              @RequestBody @Valid UpdateBalance updateBalance) {
        return walletService.updateBalanceByUserId(userId, updateBalance);
    }
}
