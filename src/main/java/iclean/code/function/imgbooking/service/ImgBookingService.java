package iclean.code.function.imgbooking.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.imgbooking.AddImgBooking;
import iclean.code.data.dto.request.imgbooking.UpdateImgBooking;
import org.springframework.http.ResponseEntity;

public interface ImgBookingService {
    ResponseEntity<ResponseObject> getAllImgBooking();

    ResponseEntity<ResponseObject> getImgBookingById(int imgBookingId);

    ResponseEntity<ResponseObject> addImgBooking(AddImgBooking request);

    ResponseEntity<ResponseObject> updateImgBooking(int imgBookingId, UpdateImgBooking request);

    ResponseEntity<ResponseObject> deleteImgBooking(int imgBookingId);
}
