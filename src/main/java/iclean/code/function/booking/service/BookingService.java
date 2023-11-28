package iclean.code.function.booking.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookingService {
    ResponseEntity<ResponseObject> getBookings(Integer userId, Pageable pageable, List<String> statuses, Boolean isHelper, String startDate, String endDate);

    ResponseEntity<ResponseObject> getBookingDetailByBookingId(Integer bookingId, Integer userId);

    ResponseEntity<ResponseObject> createServiceToCart(AddBookingRequest bookingRequest, Integer userId);

    ResponseEntity<ResponseObject> getCart(Integer userId , Boolean usingPoint);

    ResponseEntity<ResponseObject> deleteAllOnCart(Integer userId);

    ResponseEntity<ResponseObject> deleteServiceOnCart(Integer userId, Integer cartItemId);

    ResponseEntity<ResponseObject> checkoutCart(Integer userId, CheckOutCartRequest request);

    ResponseEntity<ResponseObject> acceptOrRejectBooking(Integer bookingId, AcceptRejectBookingRequest request, Integer managerId);

    ResponseEntity<ResponseObject> cancelBooking(Integer bookingId, Integer renterId);

    ResponseEntity<ResponseObject> getCheckoutCart(Integer renterId);

    ResponseEntity<ResponseObject> updateCheckoutCart(Integer renterId, CheckOutCartRequest request);

    ResponseEntity<ResponseObject> createBookingNow(CreateBookingRequestNow request, Integer renterId);

    ResponseEntity<ResponseObject> resendBooking(CheckOutCartRequest request, Integer id, Integer renterId);

    ResponseEntity<ResponseObject> getBookingNow(CreateBookingRequestNow request, Integer renterId);
}
