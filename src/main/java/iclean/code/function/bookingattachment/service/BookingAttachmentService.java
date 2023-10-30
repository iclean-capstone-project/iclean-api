package iclean.code.function.bookingattachment.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.bookingattachment.AddBookingAttachment;
import iclean.code.data.dto.request.bookingattachment.UpdateBookingAttachment;
import org.springframework.http.ResponseEntity;

public interface BookingAttachmentService {
    ResponseEntity<ResponseObject> getAllImgBooking();

    ResponseEntity<ResponseObject> getImgBookingById(int imgBookingId);

    ResponseEntity<ResponseObject> addImgBooking(AddBookingAttachment request);

    ResponseEntity<ResponseObject> updateImgBooking(int imgBookingId, UpdateBookingAttachment request);

    ResponseEntity<ResponseObject> deleteImgBooking(int imgBookingId);
}
