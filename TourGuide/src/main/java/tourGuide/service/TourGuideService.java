package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.DTO.LocationDTO;
import tourGuide.DTO.NearbyAttractionDto;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class TourGuideService {
    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    public final Tracker tracker;
    //private final GpsUtil gpsUtil;
    private final RewardsService rewardsService;
    private final TripPricer tripPricer = new TripPricer();
    boolean testMode = true;
    private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);
    @Autowired
    private UserService userService;
    @Autowired
    private GpsUtilService gpsUtilService;

    public TourGuideService(GpsUtilService gpsUtilService, RewardsService rewardsService) {
        this.gpsUtilService = gpsUtilService;
        this.rewardsService = rewardsService;
        this.userService = new UserService(gpsUtilService, rewardsService);

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            userService.initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this, userService);
        addShutDownHook();
    }

    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    public List<Provider> getTripDeals(User user) {
        int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();

        List<Provider> providers = tripPricer.getPrice(
                tripPricerApiKey,
                user.getUserId(),
                user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(),
                user.getUserPreferences().getTripDuration(),
                cumulativeRewardPoints
        );

        user.setTripDeals(providers);
        return providers;
    }

    public NearbyAttractionDto getNearByAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        Map<Double, Attraction> distancesToAttractions = new TreeMap<>();
        List<String> attractionNames = new ArrayList<>();
        List<LocationDTO> attractionLocation = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        List<Integer> rewardPoints = new ArrayList<>();

        LocationDTO userPosition = new LocationDTO(visitedLocation.location.longitude, visitedLocation.location.latitude);

        GpsUtilService.getAttractions().parallelStream().forEach(attraction -> {
            if (rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
                double distance = rewardsService.getDistance(attraction, visitedLocation.location);
                distancesToAttractions.put(distance, attraction);
            }
        });

        distancesToAttractions.entrySet().parallelStream().forEach(attraction -> {
            if (nearbyAttractions.size() >= 5) {
                return;
            }
            distances.add(attraction.getKey());
            nearbyAttractions.add(attraction.getValue());
            attractionNames.add(attraction.getValue().attractionName);
            attractionLocation.add(new LocationDTO(attraction.getValue().longitude, attraction.getValue().latitude));
        });

        return new NearbyAttractionDto(attractionNames, attractionLocation, userPosition, distances, rewardPoints);
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

}
