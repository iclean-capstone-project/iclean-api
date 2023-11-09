package iclean.code.function.booking.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.*;
import iclean.code.data.dto.response.booking.GetBookingHistoryResponse;
import iclean.code.data.dto.response.booking.GetBookingResponse;
import iclean.code.function.booking.service.BookingService;
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
@RequestMapping("api/v1/booking")
@Tag(name = "Booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    @Operation(summary = "Get all booking of a user", description = "Return all booking information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'manager')")
    public ResponseEntity<ResponseObject> getBookings(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "isAll", defaultValue = "10") boolean isAll,
            @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetBookingResponse.class) List<String> sortFields,
            Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);

        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
        }
        return bookingService.getBookings(JwtUtils.decodeToAccountId(authentication), pageable, isAll);
    }

    @GetMapping("/helper")
    @Operation(summary = "Get all booking of a user", description = "Return all booking information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('employee')")
    public ResponseEntity<ResponseObject> getBookingsAround(Authentication authentication) {
        return bookingService.getBookingsAround(JwtUtils.decodeToAccountId(authentication));
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
    public ResponseEntity<ResponseObject> acceptBookingForHelper(@RequestBody CreateBookingHelperRequest request, Authentication authentication) {
        return bookingService.acceptBookingForHelper(request, JwtUtils.decodeToAccountId(authentication));
    }

    @GetMapping("/cart")
    @Operation(summary = "Get cart of a user", description = "Return Cart information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('renter', 'employee', 'manager')")
    public ResponseEntity<ResponseObject> getCart(Authentication authentication) {
        return bookingService.getCart(JwtUtils.decodeToAccountId(authentication));
    }

    @GetMapping(value = "/history")
    @PreAuthorize("hasAnyAuthority('renter', 'employee')")
    @Operation(summary = "Get all booking of a user", description = "Return all booking information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> getBookingHistory(
            @RequestParam(name = "status") String status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetBookingHistoryResponse.class) List<String> sortFields,
            Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);

        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
        }
        return bookingService.getBookingHistory(JwtUtils.decodeToAccountId(authentication), status, pageable);
    }

    @GetMapping(value = "{bookingId}")
    @PreAuthorize("hasAnyAuthority('renter', 'manager', 'employee')")
    @Operation(summary = "Get by booking of a user", description = "Return booking information")
    public ResponseEntity<ResponseObject> getBookingByBookingId(
            @PathVariable @Valid Integer bookingId,
            Authentication authentication) {
        return bookingService.getBookingDetailById(bookingId, JwtUtils.decodeToAccountId(authentication));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('renter')")
    @Operation(summary = "Create new booking of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new booking Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> addBookings(
            @RequestBody @Valid AddBookingRequest request,
            Authentication authentication) {
        return bookingService.createServiceToCart(request, JwtUtils.decodeToAccountId(authentication));
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAnyAuthority('renter')")
    @Operation(summary = "Create new booking of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new booking Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> checkoutBooking(
            @RequestBody @Valid CheckOutCartRequest request,
            Authentication authentication) {
        return bookingService.checkoutCart(JwtUtils.decodeToAccountId(authentication), request);
    }

    //PENDING
    @PutMapping(value = "status/{bookingId}")
    @PreAuthorize("hasAnyAuthority('employee', 'manager')")
    @Operation(summary = "Update status booking of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update status booking Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateStatusBooking(
            @PathVariable("bookingId") int bookingId,
            @RequestBody @Valid UpdateStatusBookingRequest request,
            Authentication authentication) {
        return bookingService.updateStatusBooking(bookingId, JwtUtils.decodeToAccountId(authentication), request);
    }

    @PutMapping(value = "status/renter/{bookingId}")
    @PreAuthorize("hasAnyAuthority('renter')")
    @Operation(summary = "Renter Update status booking", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update status booking Successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    public ResponseEntity<ResponseObject> updateStatusBookingAsRenter(
            @PathVariable("bookingId") int bookingId,
            @RequestBody @Valid UpdateStatusBookingAsRenterRequest request,
            Authentication authentication) {
        return bookingService.updateStatusBookingAsRenter(bookingId, JwtUtils.decodeToAccountId(authentication), request);
    }

    @DeleteMapping("/cart")
    @Operation(summary = "Delete cart of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('renter')")
    public ResponseEntity<ResponseObject> deleteAllOnCart(Authentication authentication) {
        return bookingService.deleteAllOnCart(JwtUtils.decodeToAccountId(authentication));
    }

    @DeleteMapping("/cart/{id}")
    @Operation(summary = "Delete a service of a cart of a user", description = "Return message fail or successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('renter')")
    public ResponseEntity<ResponseObject> deleteServiceOnCart(Authentication authentication, @PathVariable Integer id) {
        return bookingService.deleteServiceOnCart(JwtUtils.decodeToAccountId(authentication), id);
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
        return bookingService.generateQrCode(JwtUtils.decodeToAccountId(authentication), detailId);
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
        return bookingService.validateBookingToStart(JwtUtils.decodeToAccountId(authentication), detailId, request);
    }
}
