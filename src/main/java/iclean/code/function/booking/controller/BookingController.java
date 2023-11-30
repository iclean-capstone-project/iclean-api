package iclean.code.function.booking.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.*;
import iclean.code.data.dto.response.booking.GetBookingResponse;
import iclean.code.function.booking.service.BookingService;
import iclean.code.utils.validator.ValidInputList;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/booking")
@Tag(name = "Booking")
@Validated
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @GetMapping
    @Operation(summary = "Get all booking of a user or app if manager", description = "Return all booking information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'manager', 'admin')")
    public ResponseEntity<ResponseObject> getBookings(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "statuses", required = false)
            @ValidInputList(value = "(?i)(rejected|not_yet|approved" +
                    "|finished|no_money|canceled)", message = "Booking Status is not valid")
            List<String> statuses,
            @RequestParam(name = "isHelper", defaultValue = "false") Boolean isHelper,
            @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetBookingResponse.class) List<String> sortFields,
            Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);

        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields, GetBookingResponse.class);
        }
        return bookingService.getBookings(JwtUtils.decodeToAccountId(authentication), pageable, statuses, isHelper, startDate, endDate);
    }

    @GetMapping("/cart")
    @Operation(summary = "Get cart of a user", description = "Return Cart information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'manager', 'admin')")
    public ResponseEntity<ResponseObject> getCart(Authentication authentication,
                                                  @RequestParam(required = false, defaultValue = "false")
                                                  Boolean usingPoint) {
        return bookingService.getCart(JwtUtils.decodeToAccountId(authentication), usingPoint);
    }

    @GetMapping(value = "{bookingId}")
    @PreAuthorize("hasAnyAuthority('renter', 'manager', 'employee', 'admin')")
    @Operation(summary = "Get by booking of a user", description = "Return booking information")
    public ResponseEntity<ResponseObject> getBookingByBookingId(
            @PathVariable @Valid Integer bookingId,
            Authentication authentication) {
        return bookingService.getBookingDetailByBookingId(bookingId, JwtUtils.decodeToAccountId(authentication));
    }

    @PutMapping(value = "manager/{bookingId}")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accept/Reject successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @Operation(summary = "Accept or reject a booking by manager", description = "Return message fail or successful")
    public ResponseEntity<ResponseObject> acceptOrRejectBooking(
            @PathVariable @Valid Integer bookingId,
            @RequestBody @Valid AcceptRejectBookingRequest request,
            Authentication authentication) {
        return bookingService.acceptOrRejectBooking(bookingId, request, JwtUtils.decodeToAccountId(authentication));
    }

    @PostMapping("/cart")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Create new booking of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new booking Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> addServiceToCart(
            @RequestBody @Valid AddBookingRequest request,
            Authentication authentication) {
        return bookingService.createServiceToCart(request, JwtUtils.decodeToAccountId(authentication));
    }

    @PostMapping("resend/{id}")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Resend a booking reject by manager for renter", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new booking Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> resendBooking(@RequestBody(required = false) CheckOutCartRequest request,
            Authentication authentication, @PathVariable Integer id) {
        return bookingService.resendBooking(request , id, JwtUtils.decodeToAccountId(authentication));
    }

    @PostMapping("/request-now")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Create new booking of a user (not add to cart)", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new booking Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getServiceAddNow(
            @RequestBody @Valid CreateBookingRequestNow request,
            Authentication authentication) {
        return bookingService.getBookingNow(request, JwtUtils.decodeToAccountId(authentication));
    }

    @PostMapping("/checkout-now")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Create new booking of a user (not add to cart)", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new booking Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> createBookingNow(
            @RequestBody @Valid CreateBookingRequestNow request,
            Authentication authentication) {
        return bookingService.createBookingNow(request, JwtUtils.decodeToAccountId(authentication));
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Create new booking of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new booking Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> checkoutBooking(
            @RequestBody(required = false) @Valid CheckOutCartRequest request,
            Authentication authentication) {
        return bookingService.checkoutCart(JwtUtils.decodeToAccountId(authentication), request);
    }

    @PutMapping("/checkout")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Update a booking of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return information checkout booking"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateCheckoutBooking(
            @RequestBody(required = false) @Valid CheckOutCartRequest request,
            Authentication authentication) {
        return bookingService.updateCheckoutCart(JwtUtils.decodeToAccountId(authentication), request);
    }

    @GetMapping("/checkout")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Return information checkout booking", description = "Return information checkout booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return information checkout booking"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getCheckoutBooking(
            Authentication authentication) {
        return bookingService.getCheckoutCart(JwtUtils.decodeToAccountId(authentication));
    }

    @DeleteMapping("/cart")
    @Operation(summary = "Delete cart of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    public ResponseEntity<ResponseObject> deleteAllOnCart(Authentication authentication) {
        return bookingService.deleteAllOnCart(JwtUtils.decodeToAccountId(authentication));
    }

    @DeleteMapping("{bookingId}")
    @Operation(summary = "Cancel a booking by user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    public ResponseEntity<ResponseObject> cancelBooking(Authentication authentication,
                                                        @PathVariable Integer bookingId) {
        return bookingService.cancelBooking(bookingId, JwtUtils.decodeToAccountId(authentication));
    }

    @DeleteMapping("/cart/{id}")
    @Operation(summary = "Delete a service of a cart of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    public ResponseEntity<ResponseObject> deleteServiceOnCart(Authentication authentication, @PathVariable Integer id) {
        return bookingService.deleteServiceOnCart(JwtUtils.decodeToAccountId(authentication), id);
    }
}
