package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
    private final int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private final RewardCentral rewardsCentral;
    @Autowired
    private GpsUtilService gpsUtilService;

    public RewardsService(GpsUtilService gpsUtilService, RewardCentral rewardCentral) {
        this.gpsUtilService = gpsUtilService;
        this.rewardsCentral = rewardCentral;
    }

    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }

    public void calculateRewards(User user) {
        List<VisitedLocation> userLocations = user.getVisitedLocations();
        List<Attraction> attractions = gpsUtilService.getAttractions();

        // thread-safe map
        Map<String, UserReward> userRewards = new ConcurrentHashMap<>();
        // parallel loop
        userLocations.parallelStream().forEach(visitedLocation -> {
            attractions.parallelStream().forEach(attraction -> {
                if (user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))) {
                    if (nearAttraction(visitedLocation, attraction)) {
                        userRewards.put(attraction.attractionName,
                                new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
                    }
                }
            });
        });

        user.setUserRewards(new ArrayList<>(userRewards.values()));
    }

    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        int attractionProximityRange = 200;
        return !(getDistance(attraction, location) > attractionProximityRange);
    }

    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return !(getDistance(attraction, visitedLocation.location) > proximityBuffer);
    }

    public int getRewardPoints(Attraction attraction, User user) {
        return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
    }

    public double getDistance(Location visitedLocation, Location attraction) {
        double lat1 = Math.toRadians(visitedLocation.latitude);
        double lon1 = Math.toRadians(visitedLocation.longitude);
        double lat2 = Math.toRadians(attraction.latitude);
        double lon2 = Math.toRadians(attraction.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }

}
