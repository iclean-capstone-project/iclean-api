package iclean.code.function.bookingdetail.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.booking.QRCodeValidate;
import iclean.code.data.dto.response.bookingdetail.UpdateBookingDetailRequest;
import org.springframework.http.ResponseEntity;

public interface BookingDetailService {
    ResponseEntity<ResponseObject> cancelBookingDetail(Integer renterId, Integer detailId);
    ResponseEntity<ResponseObject> cancelBookingDetailByHelper(Integer helperId, Integer detailId);
    ResponseEntity<ResponseObject> updateBookingDetail(int detailId, Integer renterId, UpdateBookingDetailRequest request);
    ResponseEntity<ResponseObject> validateBookingToStart(Integer userId, Integer detailId, QRCodeValidate request);
    ResponseEntity<ResponseObject> generateQrCode(Integer renterId, Integer detailId);

}
