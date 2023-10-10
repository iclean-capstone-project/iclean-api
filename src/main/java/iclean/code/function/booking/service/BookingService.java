package iclean.code.function.booking.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.AddBookingRequest;
import iclean.code.data.dto.request.booking.UpdateStatusBookingRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface BookingService {
    ResponseEntity<ResponseObject> getAllBooking(Integer userId, Pageable pageable);

    ResponseEntity<ResponseObject> getBookingById(Integer bookingId, Integer userId, Pageable pageable);

    ResponseEntity<ResponseObject> addBooking(AddBookingRequest bookingRequest, Integer userId);

    ResponseEntity<ResponseObject> updateStatusBooking(Integer bookingId, Integer userId, UpdateStatusBookingRequest bookingRequest);

    ResponseEntity<ResponseObject> deleteBooking(int bookingId);
}
