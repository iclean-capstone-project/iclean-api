package iclean.code.function.moneypoint.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.moneypoint.CreateMoneyPoint;
import iclean.code.data.dto.request.moneypoint.UpdateMoneyPoint;
import iclean.code.data.dto.response.address.GetAddressResponseDto;
import iclean.code.function.moneypoint.service.MoneyPointService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/moneyPoint")
@Tag(name = "Money Point")
public class MoneyPointController {

    @Autowired
    private MoneyPointService moneyPointService;

    @GetMapping
    @Operation(summary = "Get All Money Point ", description = "Return All Money Point")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get All Money Point success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    public ResponseEntity<ResponseObject> getAllMoneyPoint(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetAddressResponseDto.class) List<String> sortFields) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);

        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
        }
        return moneyPointService.getAllMoneyPoint(pageable);
    }

    @GetMapping(value = "/renter")
    @Operation(summary = "Get Money Point By Renter ", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Money Point success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter')")
    public ResponseEntity<ResponseObject> getMoneyPointByRenter(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetAddressResponseDto.class) List<String> sortFields,
            Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);

        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
        }
        return moneyPointService.getMoneyPointByRenter(JwtUtils.decodeToAccountId(authentication), pageable);
    }

//    @GetMapping(value = "{userId}")
//    public ResponseEntity<ResponseObject> getMoneyPointByUserId(@PathVariable("userId") @Valid int userId) {
//        return moneyPointService.getMoneyPointByUserId(userId);
//    }

    @PostMapping
    @Operation(summary = "Add Money Point", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add Money Point success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    public ResponseEntity<ResponseObject> addNewMoneyPoint(@RequestBody @Valid CreateMoneyPoint moneyPoint) {
        return moneyPointService.addNewMoneyPoint(moneyPoint);
    }

    @PutMapping(value = "{userId}")
    @Operation(summary = "Update Money Point", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update Money Point success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    public ResponseEntity<ResponseObject> updateMoneyPointByUserId(@PathVariable("userId") int userId,
                                                                   @RequestBody @Valid UpdateMoneyPoint moneyPoint) {
        return moneyPointService.updateMoneyPointByUserId(userId, moneyPoint);
    }

    @DeleteMapping(value = "{moneyPointById}")
    @Operation(summary = "Delete Money Point", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete Money Point success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> deleteMoneyPoint(@PathVariable("moneyPointById") @Valid int moneyPointById) {
        return moneyPointService.deleteMoneyPoint(moneyPointById);
    }
}
