package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import rewardCentral.RewardCentral;
import tourGuide.DTO.LocationDTO;
import tourGuide.repository.AttractionRepository;
import tourGuide.repository.UserRepository;
import tourGuide.service.*;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tripPricer.TripPricer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TourGuideControllerTest {

    @Mock
    private TourGuideService tourGuideService;

    @Mock
    private RewardsService rewardsService;

    @Mock
    private GpsUtilService gpsUtilService;

    @Mock
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;


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
    }

    @Test
    public void indexTest() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Greetings from TourGuide!"));
    }

    @Test
    public void getLocationTest() throws Exception {
        String userName = "john";
        User user = new User(UUID.randomUUID(), userName, "000", "jon@tourGuide.com");
        userService.generateUserLocationHistory(user);
        userService.addUser(user);

        mockMvc.perform(get("/getLocation").param("userName", userName))
                .andExpect(status().isOk());
    }

    @Test
    public void getNearbyAttractionsTest() throws Exception {
        String userName = "john";
        User user = new User(UUID.randomUUID(), userName, "000", "jon@tourGuide.com");
        userService.generateUserLocationHistory(user);
        userService.addUser(user);

        mockMvc.perform(get("/getNearbyAttractions").param("userName", userName))
                .andExpect(status().isOk());
    }

    @Test
    public void getRewardsTest() throws Exception {
        String userName = "john";
        User user = new User(UUID.randomUUID(), userName, "000", "jon@tourGuide.com");
        userService.generateUserLocationHistory(user);
        userService.addUser(user);

        mockMvc.perform(get("/getRewards").param("userName", userName))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllCurrentLocationsTest() throws Exception {
        mockMvc.perform(get("/getAllCurrentLocations"))
                .andExpect(status().isOk());
    }

    @Test
    public void getTripDealsTest() throws Exception {
        String userName = "john";
        User user = new User(UUID.randomUUID(), userName, "000", "jon@tourGuide.com");
        userService.generateUserLocationHistory(user);
        userService.addUser(user);

        mockMvc.perform(get("/getTripDeals").param("userName", userName))
                .andExpect(status().isOk());
    }

//    @Test
//    public void updateUserPreferencesTest() throws Exception {
//        String userName = "john";
//        User user = new User(UUID.randomUUID(), userName, "000", "jon@tourGuide.com");
//        userService.generateUserLocationHistory(user);
//        UserPreferences userPreferences = ;
//        userService.addUser(user);
//
//        mockMvc.perform(get("/preferences").param("userName", userName))
//                .andExpect(status().isOk());
//    }
}
