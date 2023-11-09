package iclean.code.function.bookingdetailhelper.service;

import iclean.code.data.dto.common.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface BookingDetailHelperService {
    ResponseEntity<ResponseObject> getHelpersForABooking(Integer detailId, Integer userId);
}
