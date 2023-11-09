package iclean.code.function.booking.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface BookingService {
    ResponseEntity<ResponseObject> getBookings(Integer userId, Pageable pageable, boolean isAll);
    ResponseEntity<ResponseObject> getBookingsAround(Integer userId);

    ResponseEntity<ResponseObject> getBookingDetailById(Integer bookingId, Integer userId);

    ResponseEntity<ResponseObject> createServiceToCart(AddBookingRequest bookingRequest, Integer userId);

    ResponseEntity<ResponseObject> getCart(Integer userId);

    ResponseEntity<ResponseObject> deleteAllOnCart(Integer userId);

    ResponseEntity<ResponseObject> deleteServiceOnCart(Integer userId, Integer detailId);

    ResponseEntity<ResponseObject> checkoutCart(Integer userId, CheckOutCartRequest request);

    ResponseEntity<ResponseObject> updateStatusBooking(Integer bookingId, Integer userId, UpdateStatusBookingRequest bookingRequest);

    ResponseEntity<ResponseObject> updateStatusBookingAsRenter(Integer bookingId, Integer userId, UpdateStatusBookingAsRenterRequest bookingRequest);

    ResponseEntity<ResponseObject> getBookingHistory(int userId, String status, Pageable pageable);

    ResponseEntity<ResponseObject> acceptBookingForHelper(CreateBookingHelperRequest request, Integer userId);

    ResponseEntity<ResponseObject> validateBookingToStart(Integer userId, Integer detailId, QRCodeValidate request);

    ResponseEntity<ResponseObject> generateQrCode(Integer renterId, Integer detailId);
}
