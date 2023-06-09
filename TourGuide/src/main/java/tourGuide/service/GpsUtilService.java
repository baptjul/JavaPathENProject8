package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.repository.AttractionRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class GpsUtilService {

    private final Logger logger = LoggerFactory.getLogger(GpsUtilService.class);
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private final int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private static GpsUtil gpsUtil;
    private final AttractionRepository attractionRepository;

    public GpsUtilService(GpsUtil gpsUtil, AttractionRepository attractionRepository) {
        Locale.setDefault(Locale.ENGLISH);
        GpsUtilService.gpsUtil = gpsUtil;
        this.attractionRepository = attractionRepository;
    }

    public List<Attraction> getAttractions() {
        return attractionRepository.getAttractions();
    }

    public VisitedLocation getUserLocation(UUID userId) {
        return gpsUtil.getUserLocation(userId);
    }

    public double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    public double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    public Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }

    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return getDistance(attraction, location) < 200;
    }

    public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return !(getDistance(attraction, visitedLocation.location) > proximityBuffer);
    }

    public double getDistance(Location visitedLocation, Location attraction) {
        double lat1 = Math.toRadians(visitedLocation.latitude);
        double lon1 = Math.toRadians(visitedLocation.longitude);
        double lat2 = Math.toRadians(attraction.latitude);
        double lon2 = Math.toRadians(attraction.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    }
}