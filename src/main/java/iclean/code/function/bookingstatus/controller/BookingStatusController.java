package iclean.code.function.bookingstatus.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.bookingStatus.AddBookingStatusRequest;
import iclean.code.data.dto.request.bookingStatus.UpdateBookingStatusRequest;
import iclean.code.function.bookingstatus.service.BookingStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/bookingStatus")
@Tag(name = "Booking Status")
public class BookingStatusController {

    @Autowired
    private BookingStatusService bookingStatusService;

    @GetMapping
    @Operation(summary = "Get all booking status ", description = "Return all booking status information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Status Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> getAllBookingStatus() {
        return bookingStatusService.getAllBookingStatus();
    }

    @GetMapping(value = "{bookingStatusId}")
    @Operation(summary = "Get booking status by booking id", description = "Return all booking status information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Status Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> getBookingByBookingId(@PathVariable("bookingStatusId") @Valid int bookingStatusId) {
        return bookingStatusService.getBookingStatusById(bookingStatusId);
    }

    @PostMapping
    @Operation(summary = "Add booking status ", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Status Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> addBookingStatus(@RequestBody @Valid AddBookingStatusRequest request) {
        return bookingStatusService.addBookingStatus(request);
    }

    @PutMapping(value = "status/{bookingStatusId}")
    @Operation(summary = "Update booking status ", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Status Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> updateStatusBooking(@PathVariable("bookingStatusId") int bookingStatusId,
                                                              @RequestBody @Valid UpdateBookingStatusRequest request) {
        return bookingStatusService.updateBookingStatus(bookingStatusId, request);
    }

    @DeleteMapping(value = "{bookingStatusId}")
    @Operation(summary = "Get all booking status by booking id", description = "Return message fail or success")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking Status Information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<ResponseObject> deleteBookingStatus(@PathVariable("bookingStatusId") @Valid int bookingStatusId) {
        return bookingStatusService.deleteBookingStatus(bookingStatusId);
    }
}
