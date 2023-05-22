package tourGuide;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.AttractionRepository;
import tourGuide.repository.UserRepository;
import tourGuide.service.*;
import tourGuide.user.User;
import gpsUtil.location.Attraction;
import tourGuide.user.UserReward;
import tripPricer.TripPricer;

public class PerformanceTest {
	
	/*
	 * A note on performance improvements:
	 *     
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *     
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *     
	 *     
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent. 
	 * 
	 *     These are performance metrics that we are trying to hit:
	 *     
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */

	private GpsUtilService gpsUtilService;
	private RewardsService rewardsService;
	private UserService userService;
	private TourGuideService tourGuideService;
	private StopWatch stopWatch;

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

		stopWatch = new StopWatch();
		InternalTestHelper.setInternalUserNumber(10000);
	}
	
	//@Ignore
	@Test
	public void highVolumeTrackLocation() {
		List<User> allUsers = userService.getAllUsers();

		stopWatch.start();
		for(User user : allUsers) {
			tourGuideService.trackUserLocation(user);
		}
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();


		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	@Test
	public void highVolumeGetRewards() {
		Attraction attraction = gpsUtilService.getAttractions().get(0);
		List<User> allUsers = userService.getAllUsers();
		allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

		stopWatch.start();
		CompletableFuture<Void> future = rewardsService.calculateMultipleRewards(allUsers);
		future.join();

		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}

		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
}
