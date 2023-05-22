package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.AttractionRepository;
import tourGuide.repository.UserRepository;
import tourGuide.service.*;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.TripPricer;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RewardsServiceTest {

    private GpsUtilService gpsUtilService;
    private RewardsService rewardsService;
    private UserService userService;
    private TourGuideService tourGuideService;

    @BeforeEach
    public void setUp() {
        GpsUtil gpsUtil = new GpsUtil();
        TripPricer tripPricer = new TripPricer();

        UserRepository userRepository = new UserRepository();
        AttractionRepository attractionRepository = new AttractionRepository(gpsUtil);

        gpsUtilService = new GpsUtilService(gpsUtil, attractionRepository);
        rewardsService = new RewardsService(gpsUtilService, new RewardCentral());
        userService = new UserService(gpsUtilService, userRepository);
        tourGuideService = new TourGuideService(gpsUtilService, new TripPricerService(tripPricer), rewardsService, userService);

        InternalTestHelper.setInternalUserNumber(0);
    }

    @Test
    public void userGetRewards() {
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Attraction attraction = gpsUtilService.getAttractions().get(0);
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));

        CompletableFuture<Void> rewards = rewardsService.calculateRewards(user);
        rewards.join();
        List<UserReward> userRewards = user.getUserRewards();
        tourGuideService.tracker.stopTracking();
		assertEquals(1, userRewards.size());
    }

    @Test
    public void isWithinAttractionProximity() {
        Attraction attraction = gpsUtilService.getAttractions().get(0);
        assertTrue(gpsUtilService.isWithinAttractionProximity(attraction, attraction));
    }

    @Test
    public void nearAllAttractions() {
        gpsUtilService.setProximityBuffer(Integer.MAX_VALUE);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        userService.addUser(user);

        CompletableFuture<List<Attraction>> allAttractionsFuture = CompletableFuture.supplyAsync(() -> gpsUtilService.getAttractions());

        CompletableFuture<List<VisitedLocation>> addLocationsFuture = allAttractionsFuture.thenApplyAsync(allAttractions  -> {
            allAttractions.forEach(attraction -> {
                Location location = new Location(attraction.latitude, attraction.longitude);
                user.addToVisitedLocations(new VisitedLocation(user.getUserId(), location, new Date()));
            });

            return user.getVisitedLocations();
        });

        CompletableFuture<Void> rewardsFuture = addLocationsFuture.thenComposeAsync(userVisitedLocations -> {
            User userTarget = userService.getUser(user.getUserName());

            List<User> singleUserList = Collections.singletonList(userTarget);
            return rewardsService.calculateMultipleRewards(singleUserList);
        });

        rewardsFuture.join();
        List<UserReward> userRewards = userService.getUserRewards(user);
        tourGuideService.tracker.stopTracking();

        assertEquals(gpsUtilService.getAttractions().size(), userRewards.size());
    }
}
