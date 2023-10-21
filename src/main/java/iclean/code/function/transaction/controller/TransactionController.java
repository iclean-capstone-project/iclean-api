package iclean.code.function.transaction.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.transaction.TransactionRequestDto;
import iclean.code.function.transaction.service.TransactionService;
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
    public ResponseEntity<ResponseObject> getTransactions(Authentication authentication) {
        return transactionService.getTransactions(JwtUtils.decodeToAccountId(authentication));
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
    public ResponseEntity<ResponseObject> createTransaction(@RequestBody @Valid TransactionRequestDto request) {
        return transactionService.createTransaction(request);
    }
}
