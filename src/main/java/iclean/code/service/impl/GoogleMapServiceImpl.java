package iclean.code.service.impl;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import iclean.code.data.dto.common.Position;
import iclean.code.data.dto.common.UserPosition;
import iclean.code.service.GoogleMapService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class GoogleMapServiceImpl implements GoogleMapService {
    @Value("${iclean.app.google.api.key}")
    private String googleApiKey;
    private GeoApiContext context;
    @PostConstruct
    private void initialize() {
        try {
            this.context = new GeoApiContext.Builder().apiKey(googleApiKey).build();
        } catch (Exception e) {
            log.error("Create Google Map Error", e);
        }
    }
    public Double checkDistance(Position origin, Position destination) {
        double distance = 0D;
        try {
            LatLng latLng = new LatLng(origin.getLatitude(), origin.getLongitude());
            LatLng dest = new LatLng(destination.getLatitude(), destination.getLongitude());
            DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(context)
                    .origins(latLng)
                    .destinations(dest)
                    .await();
            distance = (double) (distanceMatrix.rows[0].elements[0].distance.inMeters / 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return distance;
    }

    private Double calculateDistance(Position reference, Position position) {
        double earthRadius = 6371;

        double dLat = Math.toRadians(reference.getLongitude() - position.getLongitude());
        double dLng = Math.toRadians(reference.getLatitude() - position.getLatitude());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(reference.getLatitude())) *
                        Math.cos(Math.toRadians(position.getLatitude())) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }
    @Override
    public List<UserPosition> checkDistance(List<UserPosition> positionList, Position position, double maxDistance) {
        List<UserPosition> filteredPositions = new ArrayList<>();

        for (UserPosition element : positionList) {
            double distance = calculateDistance(element.getPosition(), position);
            if (distance <= maxDistance) {
                filteredPositions.add(element);
            }
        }

        return filteredPositions;
    }
}
