package tourGuide;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.List;
import java.util.UUID;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;
import tourGuide.DTO.NearbyAttractionDto;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tripPricer.Provider;

public class TestTourGuideService {

//	@Test
//	public void getUserLocation() {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
//		UserService userService = new UserService(gpsUtil, rewardsService);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//
//		VisitedLocation visitedLocation = userService.trackUserLocation(user);
//		tourGuideService.tracker.stopTracking();
//		assertEquals(visitedLocation.userId, user.getUserId());
//	}
//
//	@Test
//	public void addUser() {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		//TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
//		UserService userService = new UserService(gpsUtil, rewardsService);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
//
//		userService.addUser(user);
//		userService.addUser(user2);
//
//		User retrivedUser = userService.getUser(user.getUserName());
//		User retrivedUser2 = userService.getUser(user2.getUserName());
//
//		userService.tracker.stopTracking();
//
//		assertEquals(user, retrivedUser);
//		assertEquals(user2, retrivedUser2);
//	}
//
//	@Test
//	public void getAllUsers() {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		//TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
//		UserService userService = new UserService(gpsUtil, rewardsService);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
//
//		userService.addUser(user);
//		userService.addUser(user2);
//
//		List<User> allUsers = userService.getAllUsers();
//
//		userService.tracker.stopTracking();
//
//		assertTrue(allUsers.contains(user));
//		assertTrue(allUsers.contains(user2));
//	}
//
//	@Test
//	public void trackUser() {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
//		UserService userService = new UserService(gpsUtil, rewardsService);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		VisitedLocation visitedLocation = userService.trackUserLocation(user);
//
//		tourGuideService.tracker.stopTracking();
//
//		assertEquals(user.getUserId(), visitedLocation.userId);
//	}
//
//	//@Ignore // Not yet implemented
//	@Test
//	public void getNearbyAttractions() {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
//		UserService userService = new UserService(gpsUtil, rewardsService);
//		userService.tracker.stopTracking();
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//		VisitedLocation visitedLocation = userService.trackUserLocation(user);
//
//
//		NearbyAttractionDto attractions = tourGuideService.getNearByAttractions(visitedLocation);
//
//		assertEquals(5, attractions.getAttractionNames().size());
//	}
//
//	public void getTripDeals() {
//		GpsUtil gpsUtil = new GpsUtil();
//		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
//		InternalTestHelper.setInternalUserNumber(0);
//		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
//
//		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//
//		List<Provider> providers = tourGuideService.getTripDeals(user);
//
//		tourGuideService.tracker.stopTracking();
//
//		assertEquals(10, providers.size());
//	}
//
	
}
