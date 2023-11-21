package iclean.code.service;

import iclean.code.data.domain.BookingDetail;
import iclean.code.data.dto.common.Position;
import iclean.code.data.dto.response.booking.GetBookingResponseForHelper;

import java.util.List;

public interface GoogleMapService {
    public List<BookingDetail> checkDistance(List<BookingDetail> positionList,
                                            Position position,
                                            double maxDistance);
    public Double checkDistance(Position src, Position des);
}
