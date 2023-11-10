package iclean.code.function.bookingdetail.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.QRCodeValidate;
import iclean.code.data.dto.response.bookingdetail.UpdateBookingDetailRequest;
import iclean.code.function.bookingdetail.service.BookingDetailService;
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
@RequestMapping("api/v1/booking-detail")
@Tag(name = "Booking Detail")
public class BookingDetailController {
    @Autowired
    private BookingDetailService bookingDetailService;
    @DeleteMapping("/renter-cancellation/{detailId}")
    @Operation(summary = "Delete a service of a cart of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter')")
    public ResponseEntity<ResponseObject> cancelBookingDetail(Authentication authentication, @PathVariable Integer detailId) {
        return bookingDetailService.cancelBookingDetail(JwtUtils.decodeToAccountId(authentication), detailId);
    }

    @DeleteMapping("/helper-cancellation/{detailId}")
    @Operation(summary = "Delete a service of a cart of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('employee')")
    public ResponseEntity<ResponseObject> cancelBookingDetailByHelper(Authentication authentication, @PathVariable Integer detailId) {
        return bookingDetailService.cancelBookingDetailByHelper(JwtUtils.decodeToAccountId(authentication), detailId);
    }

    @PutMapping(value = "{detailId}")
    @PreAuthorize("hasAnyAuthority('employee', 'renter')")
    @Operation(summary = "Update status booking of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update status booking Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateBookingDetail(
            @PathVariable("detailId") int detailId,
            @RequestBody @Valid UpdateBookingDetailRequest request,
            Authentication authentication) {
        return bookingDetailService.updateBookingDetail(detailId, JwtUtils.decodeToAccountId(authentication), request);
    }

    @GetMapping("/validate/{detailId}")
    @Operation(summary = "Validate QR a booking detail to start", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('renter')")
    public ResponseEntity<ResponseObject> generateQrCode(Authentication authentication,
                                                         @PathVariable Integer detailId) {
        return bookingDetailService.generateQrCode(JwtUtils.decodeToAccountId(authentication), detailId);
    }

    @PostMapping("/validate/{detailId}")
    @Operation(summary = "Validate QR a booking detail to start", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('employee')")
    public ResponseEntity<ResponseObject> validateBookingToStart(Authentication authentication,
                                                                 @PathVariable Integer detailId,
                                                                 @RequestBody @Valid QRCodeValidate request) {
        return bookingDetailService.validateBookingToStart(JwtUtils.decodeToAccountId(authentication), detailId, request);
    }
}
