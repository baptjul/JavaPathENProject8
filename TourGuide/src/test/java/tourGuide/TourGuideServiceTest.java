package tourGuide;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;
import tourGuide.DTO.NearbyAttractionDto;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.AttractionRepository;
import tourGuide.repository.UserRepository;
import tourGuide.service.*;
import tourGuide.user.User;
import tripPricer.Provider;
import tripPricer.TripPricer;

import static org.junit.jupiter.api.Assertions.*;

public class TourGuideServiceTest {

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
    public void getUserLocation() {
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        CompletableFuture<VisitedLocation> visitedLocation = tourGuideService.trackUserLocation(user);
        tourGuideService.tracker.stopTracking();
        //assertTrue(visitedLocation.userId.equals(user.getUserId()));
		try {
			assertEquals(visitedLocation.get().userId, user.getUserId());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    }

	@Test
	public void addUser() {
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);

		User retrivedUser = userService.getUser(user.getUserName());
		User retrivedUser2 = userService.getUser(user2.getUserName());

        tourGuideService.tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	@Test
	public void getAllUsers() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);

		List<User> allUsers = userService.getAllUsers();

		tourGuideService.tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser() throws ExecutionException, InterruptedException {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user).get();

		tourGuideService.tracker.stopTracking();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() throws ExecutionException, InterruptedException {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		CompletableFuture<VisitedLocation> futureVisitedLocation  = tourGuideService.trackUserLocation(user);
		CompletableFuture<NearbyAttractionDto> futureAttractions = futureVisitedLocation.thenCompose(visitedLocation ->
				tourGuideService.getNearByAttractions(visitedLocation, user));

		CompletableFuture.allOf(futureVisitedLocation, futureAttractions).join();

		VisitedLocation visitedLocation = futureVisitedLocation.get();
		NearbyAttractionDto attractions = futureAttractions.get();

		tourGuideService.tracker.stopTracking();
		assertNotNull(visitedLocation);
		assertNotNull(attractions);
		assertEquals(5, attractions.getAttractionNames().size());
	}

	@Test
	public void getTripDeals() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, providers.size());
	}
}
