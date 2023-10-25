package iclean.code.service;

import iclean.code.data.dto.common.Position;
import iclean.code.data.dto.common.UserPosition;

import java.util.List;

public interface GoogleMapService {
    public List<UserPosition> checkDistance(List<UserPosition> positionList,
                                            Position position,
                                            double maxDistance);
    public Double checkDistance(Position src, Position des);
}
