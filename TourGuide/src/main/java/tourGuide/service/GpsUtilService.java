package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

import org.springframework.stereotype.Service;
import tourGuide.user.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class GpsUtilService {
    private static GpsUtil gpsUtil = new GpsUtil();
    public static List<Attraction> attractions = new ArrayList<>();

    public GpsUtilService(GpsUtil gpsUtil) {
        GpsUtilService.gpsUtil = gpsUtil;
    }

    public static List<Attraction> getAttractions() {
        if (attractions.size() == 0) {
            attractions = gpsUtil.getAttractions();
        }
        return attractions;
    }

    public VisitedLocation getUserLocation(User user) {
        return gpsUtil.getUserLocation(user.getUserId());
    }
}
