package iclean.code.function.transaction.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.transaction.TransactionRequest;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@RestController
@Validated
@RequestMapping("api/v1/transaction")
@Tag(name = "Transaction API")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Get all transactions of a user", description = "Return all transactions information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getTransactions(@RequestParam(value = "type")
                                                              @Schema(example = "money|point")
                                                              @Pattern(regexp = "(?i)(money|point)", message = "Wallet Type is not valid")
                                                              @NotNull(message = "Wallet Type are invalid")
                                                              String type,
                                                          @RequestParam(name = "page", defaultValue = "1") int page,
                                                          @RequestParam(name = "size", defaultValue = "10") int size,
                                                          Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);
        return transactionService.getTransactions(JwtUtils.decodeToAccountId(authentication), type, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Get a transaction of a user by id", description = "Return transaction information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getTransaction(@PathVariable Integer id,
                                                         Authentication authentication) {
        return transactionService.getTransaction(id, JwtUtils.decodeToAccountId(authentication));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    @Operation(summary = "Create new transaction of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new Transaction Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createTransaction(@RequestBody @Valid TransactionRequest request) {
        return transactionService.createTransaction(request);
    }
}
