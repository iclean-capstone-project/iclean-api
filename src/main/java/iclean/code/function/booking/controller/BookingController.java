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
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@RequestMapping("api/v1/booking")
@Tag(name = "Booking")
@Validated
public class BookingController {

    @Autowired
    private BookingService bookingService;

    //Hiển thị danh sách lịch sử toàn bộ bookings của helper, renter, của manager sẽ hiển thị toàn bộ các
    //booking của tất cả các status
    @GetMapping
    @Operation(summary = "Get all booking of a user or app if manager", description = "Return all booking information")
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
            @RequestParam(name = "status", required = false) @Schema(example = "rejected|not_yet|approved|employee_accepted|renter_canceled" +
                    "|employee_canceled|waiting|in_processing|finish|no_money|on_cart")
            @Pattern(regexp = "(?i)(rejected|not_yet|approved|waiting|employee_accepted|renter_canceled" +
                    "|employee_canceled|in_processing|finish|no_money)", message = "Booking Status is not valid")
            String status,
            @RequestParam(name = "isHelper", defaultValue = "false") Boolean isHelper,
            @RequestParam(name = "sort", required = false) @ValidSortFields(value = GetBookingResponse.class) List<String> sortFields,
            Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);

        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields, GetBookingResponse.class);
        }
        return bookingService.getBookings(JwtUtils.decodeToAccountId(authentication), pageable, status, isHelper);
    }

    //Lấy danh sách các booking detail đã đc apporve nhưng chưa có người nhận để có thể cho helper nhận
    //booking đó
    //done
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

    @PutMapping("/make-payment/{bookingId}")
    @Operation(summary = "Payment booking by renter", description = "Return all booking information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('employee', 'renter')")
    public ResponseEntity<ResponseObject> paymentABooking(Authentication authentication, @PathVariable Integer bookingId,
                                                          @RequestBody @Valid PaymentBookingRequest request) {
        return bookingService.paymentBooking(bookingId, JwtUtils.decodeToAccountId(authentication), request);
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
    public ResponseEntity<ResponseObject> getCart(Authentication authentication, @RequestParam(required = false, defaultValue = "false") Boolean usingPoint) {
        return bookingService.getCart(JwtUtils.decodeToAccountId(authentication), usingPoint);
    }

    @GetMapping(value = "{bookingId}")
    @PreAuthorize("hasAnyAuthority('renter', 'manager', 'employee')")
    @Operation(summary = "Get by booking of a user", description = "Return booking information")
    public ResponseEntity<ResponseObject> getBookingByBookingId(
            @PathVariable @Valid Integer bookingId,
            Authentication authentication) {
        return bookingService.getBookingDetailById(bookingId, JwtUtils.decodeToAccountId(authentication));
    }

    @PutMapping(value = "manager/{bookingId}")
    @PreAuthorize("hasAnyAuthority('manager')")
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
    public ResponseEntity<ResponseObject> addBookings(
            @RequestBody @Valid AddBookingRequest request,
            Authentication authentication) {
        return bookingService.createServiceToCart(request, JwtUtils.decodeToAccountId(authentication));
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
            @RequestBody @Valid CheckOutCartRequest request,
            Authentication authentication) {
        return bookingService.checkoutCart(JwtUtils.decodeToAccountId(authentication), request);
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
