package iclean.code.function.bookingdetail.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.CheckOutCartRequest;
import iclean.code.data.dto.request.booking.CreateBookingHelperRequest;
import iclean.code.data.dto.request.booking.QRCodeValidate;
import iclean.code.data.dto.request.bookingdetail.HelperChoiceRequest;
import iclean.code.data.dto.request.bookingdetail.ResendBookingDetailRequest;
import iclean.code.data.dto.response.bookingdetail.UpdateBookingDetailRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookingDetailService {
    ResponseEntity<ResponseObject> cancelBookingDetail(Integer renterId, Integer detailId);
    ResponseEntity<ResponseObject> cancelBookingDetailByHelper(Integer helperId, Integer detailId);
    ResponseEntity<ResponseObject> updateBookingDetail(int detailId, Integer renterId, UpdateBookingDetailRequest request);
    ResponseEntity<ResponseObject> validateBookingToStart(Integer userId, Integer detailId, QRCodeValidate request);
    ResponseEntity<ResponseObject> generateQrCode(Integer renterId, Integer detailId);
    ResponseEntity<ResponseObject> getHelpersInformation(Integer renterId, Integer bookingDetailId);
    ResponseEntity<ResponseObject> getBookingsAround(Integer userId);
    ResponseEntity<ResponseObject> acceptBookingForHelper(CreateBookingHelperRequest request, Integer userId);
    ResponseEntity<ResponseObject> getBookingDetails(Integer renterId, List<String> statuses, Boolean isHelper, Pageable pageable);
    ResponseEntity<ResponseObject> getBookingDetail(Integer renterId, Integer bookingDetailId);
    ResponseEntity<ResponseObject> chooseHelperForBooking(Integer renterId, Integer bookingDetailId, HelperChoiceRequest request);
    ResponseEntity<ResponseObject> resendBookingDetail(Integer renterId, ResendBookingDetailRequest request, Integer bookingDetailId);
    ResponseEntity<ResponseObject> checkoutBookingDetail(Integer id, Integer helperId);
    ResponseEntity<ResponseObject> getBookingDetailByHelper(Integer helperId, Integer bookingDetailId);
}
