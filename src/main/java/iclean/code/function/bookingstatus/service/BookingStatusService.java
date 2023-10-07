package iclean.code.function.bookingstatus.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.bookingstatus.AddBookingStatusRequest;
import iclean.code.data.dto.request.bookingstatus.UpdateBookingStatusRequest;
import org.springframework.http.ResponseEntity;

public interface BookingStatusService {
    ResponseEntity<ResponseObject> getAllBookingStatus();
    ResponseEntity<ResponseObject> getBookingStatusById(int statusId);
    ResponseEntity<ResponseObject> addBookingStatus(AddBookingStatusRequest newStatus);
    ResponseEntity<ResponseObject> updateBookingStatus(int bookingStatusId, UpdateBookingStatusRequest newStatus);
    ResponseEntity<ResponseObject> deleteBookingStatus(int jobId);
}
