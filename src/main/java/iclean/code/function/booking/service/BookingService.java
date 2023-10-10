package iclean.code.function.booking.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.AddBookingRequest;
import iclean.code.data.dto.request.booking.UpdateStatusBookingRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface BookingService {
    ResponseEntity<ResponseObject> getBookingsForManager(Integer integer, Pageable pageable, String search);

    ResponseEntity<ResponseObject> getBookingById(int bookingId);

    ResponseEntity<ResponseObject> addBooking(AddBookingRequest bookingRequest);

    ResponseEntity<ResponseObject> updateStatusBooking(int bookingId, UpdateStatusBookingRequest bookingRequest);

    ResponseEntity<ResponseObject> deleteBooking(int bookingId);
}
