package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.DTO.LocationDTO;
import tourGuide.DTO.NearbyAttractionDto;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class TourGuideService {
    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    public final Tracker tracker;
    private final RewardsService rewardsService;
    boolean testMode = true;
    private final GpsUtilService gpsUtilService;
        private final TripPricerService tripPricerService;
        private final UserService userService;

    public TourGuideService(GpsUtilService gpsUtilService, TripPricerService tripPricerService, RewardsService rewardsService, UserService userService) {
            this.gpsUtilService = gpsUtilService;
            this.rewardsService = rewardsService;
            this.tripPricerService = tripPricerService;
            this.userService = userService;

            if (testMode) {
                Logger logger = LoggerFactory.getLogger(TourGuideService.class);
                logger.info("TestMode enabled");
                logger.debug("Initializing users");
                userService.initializeInternalUsers();
                logger.debug("Finished initializing users");
            }
            tracker = new Tracker(this, userService);
            addShutDownHook();
    }

    public CompletableFuture<VisitedLocation> trackUserLocation(User user) {
        return CompletableFuture.supplyAsync(() -> {
            VisitedLocation visitedLocation = gpsUtilService.getUserLocation(user.getUserId());
            user.addToVisitedLocations(visitedLocation);
            return visitedLocation;
        }).thenApplyAsync(visitedLocation -> {
            rewardsService.calculateRewards(user);
            return visitedLocation;
        });
    }

    public VisitedLocation getUserLocation(User user) {
        if (user.getVisitedLocations().size() > 0) {
            return user.getLastVisitedLocation();
        } else {
            CompletableFuture<VisitedLocation> futureVisitedLocation = trackUserLocation(user);
            try {
                return futureVisitedLocation.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public CompletableFuture<NearbyAttractionDto> getNearByAttractions(VisitedLocation visitedLocation, User user) {
        LocationDTO userPosition = new LocationDTO(visitedLocation.location.longitude, visitedLocation.location.latitude);

        CompletableFuture<Map<Double, Attraction>> distancesFuture = CompletableFuture.supplyAsync(() -> {
            Map<Double, Attraction> distancesToAttractions = new TreeMap<>();
            gpsUtilService.getAttractions().forEach(attraction -> {
                System.out.println("attraction = " + gpsUtilService.isWithinAttractionProximity(attraction, visitedLocation.location) );
                if (gpsUtilService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
                    System.out.println("attraction = " + attraction );
                    double distance = gpsUtilService.getDistance( visitedLocation.location, attraction);
                    distancesToAttractions.put(distance, attraction);
                }
            });
            return distancesToAttractions;
        });

        return distancesFuture.thenApply(distancesToAttractions -> {
            List<Attraction> nearbyAttractions = new ArrayList<>();
            List<String> attractionNames = new ArrayList<>();
            List<LocationDTO> attractionLocation = new ArrayList<>();
            List<Double> distances = new ArrayList<>();
            List<Integer> rewardPoints = new ArrayList<>();

            distancesToAttractions.entrySet().parallelStream().forEach(attraction -> {
                if (nearbyAttractions.size() >= 5) {
                    return;
                }
                distances.add(attraction.getKey());
                nearbyAttractions.add(attraction.getValue());
                attractionNames.add(attraction.getValue().attractionName);
                attractionLocation.add(new LocationDTO(attraction.getValue().longitude, attraction.getValue().latitude));
                rewardPoints.add(rewardsService.getRewardPoints(attraction.getValue(), user));
            });

            return new NearbyAttractionDto(attractionNames, attractionLocation, userPosition, distances, rewardPoints);
        });
    }

    public List<Provider> getTripDeals(User user) {
        int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();

        List<Provider> providers = tripPricerService.getPrice(
                user,
                user.getUserId(),
                user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(),
                user.getUserPreferences().getTripDuration(),
                cumulativeRewardPoints
        );

        user.setTripDeals(providers);
        return providers;
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(tracker::stopTracking));
    }
}
