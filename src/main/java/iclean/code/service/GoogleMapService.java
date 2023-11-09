package iclean.code.service;

import iclean.code.data.dto.common.Position;
import iclean.code.data.dto.response.booking.GetBookingResponseForHelper;

import java.util.List;

public interface GoogleMapService {
    public List<GetBookingResponseForHelper> checkDistance(List<GetBookingResponseForHelper> positionList,
                                            Position position,
                                            double maxDistance);
    public Double checkDistance(Position src, Position des);
}
