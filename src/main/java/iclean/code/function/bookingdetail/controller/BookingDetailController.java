package iclean.code.function.bookingdetail.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.CreateBookingHelperRequest;
import iclean.code.data.dto.request.booking.QRCodeValidate;
import iclean.code.data.dto.request.bookingdetail.HelperChoiceRequest;
import iclean.code.data.dto.request.bookingdetail.ResendBookingDetailRequest;
import iclean.code.data.dto.response.bookingdetail.UpdateBookingDetailRequest;
import iclean.code.function.bookingdetail.service.BookingDetailService;
import iclean.code.utils.validator.ValidInputList;
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
import java.util.List;

@RestController
@RequestMapping("api/v1/booking-detail")
@Tag(name = "Booking Detail")
@Validated
public class BookingDetailController {
    @Autowired
    private BookingDetailService bookingDetailService;
    @GetMapping("/helper-selection/{id}")
    @Operation(summary = "Get all helper information accept to the booking", description = "Return all booking information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    public ResponseEntity<ResponseObject> getHelpersInformation(
            Authentication authentication, @PathVariable Integer id) {
        return bookingDetailService.getHelpersInformation(JwtUtils.decodeToAccountId(authentication), id);
    }

    @PutMapping("/helper-selection/{id}")
    @Operation(summary = "Choose a helper for a booking", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    public ResponseEntity<ResponseObject> chooseHelperForBooking(
            Authentication authentication, @PathVariable Integer id,
            @RequestBody @Valid HelperChoiceRequest request) {
        return bookingDetailService.chooseHelperForBooking(JwtUtils.decodeToAccountId(authentication), id, request);
    }

    @DeleteMapping("/renter-cancellation/{detailId}")
    @Operation(summary = "Delete a service of a cart of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
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

    @GetMapping
    @Operation(summary = "Get all booking detail information", description = "Get all booking detail information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'manager')")
    public ResponseEntity<ResponseObject> getBookingDetails(@RequestParam(name = "page", defaultValue = "1") int page,
                                                            @RequestParam(name = "size", defaultValue = "10") int size,
                                                            @RequestParam(name = "statuses", required = false)
                                                            @ValidInputList(value = "(?i)(rejected|not_yet|approved|waiting" +
                                                            "|in_process|finished|reported|CANCEL_BY_RENTER|CANCEL_BY_HELPER|" +
                                                                    "CANCEL_BY_SYSTEM)", message = "Booking Status is not valid")
                                                            List<String> statuses,
                                                            @RequestParam(name = "isHelper", defaultValue = "false")
                                                            Boolean isHelper,
                                                            Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);
        return bookingDetailService.getBookingDetails(JwtUtils.decodeToAccountId(authentication),
                statuses, isHelper, pageable);
    }

    @GetMapping("/{bookingDetailId}")
    @Operation(summary = "Get booking detail detail information", description = "Return booking detail detail information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'manager', 'employee')")
    public ResponseEntity<ResponseObject> getBookingDetail(Authentication authentication, @PathVariable Integer bookingDetailId) {
        return bookingDetailService.getBookingDetail(JwtUtils.decodeToAccountId(authentication), bookingDetailId);
    }

    @GetMapping("/helper/{bookingDetailId}")
    @Operation(summary = "Get booking detail detail information", description = "Return booking detail detail information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('employee')")
    public ResponseEntity<ResponseObject> getBookingDetailByHelper(Authentication authentication, @PathVariable Integer bookingDetailId) {
        return bookingDetailService.getBookingDetailByHelper(JwtUtils.decodeToAccountId(authentication), bookingDetailId);
    }

    @GetMapping("/helper/current-booking")
    @Operation(summary = "Get booking detail detail information", description = "Return booking detail detail information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('employee')")
    public ResponseEntity<ResponseObject> getCurrentBookingDetail(Authentication authentication) {
        return bookingDetailService.getCurrentBookingDetail(JwtUtils.decodeToAccountId(authentication));
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

    @PostMapping("/helper")
    @Operation(summary = "Get all booking of a user", description = "Return all booking information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('employee')")
    public ResponseEntity<ResponseObject> acceptBookingForHelper(@RequestBody CreateBookingHelperRequest request,
                                                                 Authentication authentication) {
        return bookingDetailService.acceptBookingForHelper(request, JwtUtils.decodeToAccountId(authentication));
    }

    @PostMapping("/helper/checkout/{id}")
    @Operation(summary = "Get all booking of a user", description = "Return all booking information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('employee')")
    public ResponseEntity<ResponseObject> checkoutBookingDetail(Authentication authentication,
                                                                @PathVariable Integer id) {
        return bookingDetailService.checkoutBookingDetail(id, JwtUtils.decodeToAccountId(authentication));
    }

    @GetMapping("/helper")
    @Operation(summary = "Get all booking around for a helper", description = "Return all booking information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('employee')")
    public ResponseEntity<ResponseObject> getBookingsAround(Authentication authentication) {
        return bookingDetailService.getBookingsAround(JwtUtils.decodeToAccountId(authentication));
    }

    @PostMapping("resend/{id}")
    @Operation(summary = "Resend a booking detail when complete or cancel by renter", description = "Return message success or fail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('employee')")
    public ResponseEntity<ResponseObject> resendBookingDetail(Authentication authentication,
                                                              @RequestBody ResendBookingDetailRequest request,
                                                              @PathVariable Integer id) {
        return bookingDetailService.resendBookingDetail(JwtUtils.decodeToAccountId(authentication), request, id);
    }
}
