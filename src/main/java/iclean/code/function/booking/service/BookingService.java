package iclean.code.function.booking.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface BookingService {
    ResponseEntity<ResponseObject> getBookings(Integer userId, Pageable pageable, String status, Boolean isHelper);
    ResponseEntity<ResponseObject> getBookingsAround(Integer userId);

    ResponseEntity<ResponseObject> getBookingDetailById(Integer bookingId, Integer userId);

    ResponseEntity<ResponseObject> createServiceToCart(AddBookingRequest bookingRequest, Integer userId);

    ResponseEntity<ResponseObject> getCart(Integer userId , Boolean usingPoint);

    ResponseEntity<ResponseObject> deleteAllOnCart(Integer userId);

    ResponseEntity<ResponseObject> deleteServiceOnCart(Integer userId, Integer detailId);

    ResponseEntity<ResponseObject> checkoutCart(Integer userId, CheckOutCartRequest request);

    ResponseEntity<ResponseObject> acceptBookingForHelper(CreateBookingHelperRequest request, Integer userId);

    ResponseEntity<ResponseObject> acceptOrRejectBooking(Integer bookingId, AcceptRejectBookingRequest request, Integer managerId);

    ResponseEntity<ResponseObject> paymentBooking(Integer bookingId, Integer renterId, PaymentBookingRequest request);

}
