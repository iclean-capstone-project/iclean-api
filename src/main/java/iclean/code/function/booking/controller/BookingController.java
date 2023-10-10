package iclean.code.function.booking.controller;

import iclean.code.config.JwtUtils;
import iclean.code.data.dto.common.PageRequestBuilder;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.AddBookingRequest;
import iclean.code.data.dto.request.booking.UpdateStatusBookingRequest;
import iclean.code.data.dto.response.address.GetAddressResponseDto;
import iclean.code.function.booking.service.BookingService;
import iclean.code.utils.validator.ValidSortFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("api/v1/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping(value = "/request")
    @PreAuthorize("hasAnyAuthority('manager')")
    public ResponseEntity<ResponseObject> getAllBookingRequest(@RequestParam(name = "search", defaultValue = "ví dụ", required = false) String search,
                                                        @RequestParam(name = "page", defaultValue = "1", required = false) @Min(value = 1, message = "Page cannot be smaller 1") Integer page,
                                                        @RequestParam(name = "size", defaultValue = "10", required = false) @Min(value = 1, message = "Size cannot be smaller 1") Integer size,
                                                        @RequestParam(name = "sort", defaultValue = "sortField_asc", required = false) @ValidSortFields(value = GetAddressResponseDto.class) List<String> sortFields,
                                                        Authentication authentication) {
        Pageable pageable = PageRequestBuilder.buildPageRequest(page, size);

        if (sortFields != null && !sortFields.isEmpty()) {
            pageable = PageRequestBuilder.buildPageRequest(page, size, sortFields);
        }
        return bookingService.getBookingsForManager(JwtUtils.decodeToAccountId(authentication), pageable, search);
    }

    @GetMapping(value = "{bookingId}")
    public ResponseEntity<ResponseObject> getBookingByBookingId(@PathVariable("bookingId") @Valid int bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @PostMapping
    public ResponseEntity<ResponseObject> addBooking(@RequestBody @Valid AddBookingRequest request) {
        return bookingService.addBooking(request);
    }

    @PutMapping(value = "status/{bookingId}")
    public ResponseEntity<ResponseObject> updateStatusBooking(@PathVariable("bookingId") int bookingId,
                                                              @RequestBody @Valid UpdateStatusBookingRequest request) {
        return bookingService.updateStatusBooking(bookingId, request);
    }
}
