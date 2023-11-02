package iclean.code.function.booking.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.AddBookingRequest;
import iclean.code.data.dto.request.booking.CheckOutCartRequest;
import iclean.code.data.dto.request.booking.UpdateStatusBookingAsRenterRequest;
import iclean.code.data.dto.request.booking.UpdateStatusBookingRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface BookingService {
    ResponseEntity<ResponseObject> getBookings(Integer userId, Pageable pageable);

    ResponseEntity<ResponseObject> getBookingById(Integer bookingId, Integer userId, Pageable pageable);

    ResponseEntity<ResponseObject> createServiceToCart(AddBookingRequest bookingRequest, Integer userId);

    ResponseEntity<ResponseObject> getCart(Integer userId);

    ResponseEntity<ResponseObject> deleteAllOnCart(Integer userId);

    ResponseEntity<ResponseObject> deleteServiceOnCart(Integer userId, Integer detailId);

    ResponseEntity<ResponseObject> checkoutCart(Integer userId, CheckOutCartRequest request);

    ResponseEntity<ResponseObject> updateStatusBooking(Integer bookingId, Integer userId, UpdateStatusBookingRequest bookingRequest);

    ResponseEntity<ResponseObject> updateStatusBookingAsRenter(Integer bookingId, Integer userId, UpdateStatusBookingAsRenterRequest bookingRequest);

    ResponseEntity<ResponseObject> getBookingHistory(int userId, Pageable pageable);

}
