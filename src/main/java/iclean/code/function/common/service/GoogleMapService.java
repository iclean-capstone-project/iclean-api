package iclean.code.function.common.service;

import iclean.code.data.domain.Address;
import iclean.code.data.domain.BookingDetail;
import iclean.code.data.domain.HelperInformation;
import iclean.code.data.dto.common.Position;

import java.util.List;

public interface GoogleMapService {
    public List<BookingDetail> checkDistance(List<BookingDetail> positionList,
                                            Position position,
                                            double maxDistance);

    public List<Address> checkDistanceHelper(List<Address> positionList,
                                             Position position,
                                             double maxDistance);
    public Double checkDistance(Position src, Position des);
}
