package iclean.code.function.booking.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.AddBookingRequest;
import iclean.code.data.dto.request.booking.UpdateStatusBookingRequest;
import iclean.code.function.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping(value = "/get")
    public ResponseEntity<ResponseObject> getAllBooking() {
        return bookingService.getAllBooking();
    }

    @GetMapping(value = "/getId")
    public ResponseEntity<ResponseObject> getBookingByBookingId(@RequestParam @Valid int bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @PostMapping(value = "/post")
    public ResponseEntity<ResponseObject> addBooking(@RequestBody @Valid AddBookingRequest request) {
        return bookingService.addBooking(request);
    }

    @PutMapping(value = "/updateStatus")
    public ResponseEntity<ResponseObject> updateStatusBooking(@RequestBody @Valid UpdateStatusBookingRequest request) {
        return bookingService.updateStatusBooking(request);
    }
}
