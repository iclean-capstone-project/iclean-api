package iclean.code.function.bookingdetailhelper.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.function.bookingdetailhelper.service.BookingDetailHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/booking-detail-helper")
@Tag(name = "Booking Detail Helper Api")
public class BookingDetailHelperController {
    @Autowired
    private BookingDetailHelperService bookingDetailHelperService;
    @GetMapping("/{bookingDetailId}")
    @Operation(summary = "Get list helper of a booking detail", description = "Return list helper of a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List helper information of a booking"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Login please"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access on this api"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing some field required")
    })
    @PreAuthorize("hasAuthority('renter')")
    public ResponseEntity<ResponseObject> getHelpersForABooking(@PathVariable Integer bookingDetailId, Authentication authentication) {
        return bookingDetailHelperService.getHelpersForABooking(bookingDetailId, JwtUtils.decodeToAccountId(authentication));
    }
}
