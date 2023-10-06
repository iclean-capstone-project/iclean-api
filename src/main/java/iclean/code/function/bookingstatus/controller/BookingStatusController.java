package iclean.code.function.bookingstatus.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.bookingStatus.AddBookingStatusRequest;
import iclean.code.data.dto.request.bookingStatus.UpdateBookingStatusRequest;
import iclean.code.function.bookingstatus.service.BookingStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/bookingStatus")
public class BookingStatusController {

    @Autowired
    private BookingStatusService bookingStatusService;

    @GetMapping
    public ResponseEntity<ResponseObject> getAllBookingStatus() {
        return bookingStatusService.getAllBookingStatus();
    }

    @GetMapping(value = "{bookingStatusId}")
    public ResponseEntity<ResponseObject> getBookingByBookingId(@PathVariable("bookingStatusId") @Valid int bookingStatusId) {
        return bookingStatusService.getBookingStatusById(bookingStatusId);
    }

    @PostMapping
    public ResponseEntity<ResponseObject> addBookingStatus(@RequestBody @Valid AddBookingStatusRequest request) {
        return bookingStatusService.addBookingStatus(request);
    }

    @PutMapping(value = "status/{bookingStatusId}")
    public ResponseEntity<ResponseObject> updateStatusBooking(@PathVariable("bookingStatusId") int bookingStatusId,
                                                              @RequestBody @Valid UpdateBookingStatusRequest request) {
        return bookingStatusService.updateBookingStatus(bookingStatusId, request);
    }

    @DeleteMapping(value = "{bookingStatusId}")
    public ResponseEntity<ResponseObject> deleteBookingStatus(@PathVariable("bookingStatusId") @Valid int bookingStatusId) {
        return bookingStatusService.deleteBookingStatus(bookingStatusId);
    }
}
