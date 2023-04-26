package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.DTO.LocationDTO;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.UserRepository;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    //private final GpsUtil gpsUtil;
    private final RewardsService rewardsService;
    public final Tracker tracker;
    boolean testMode = true;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    private TourGuideService tourGuideService;
    @Autowired
    private GpsUtilService gpsUtilService;

    public UserService(GpsUtilService gpsUtilService, RewardsService rewardsService) {
        this.gpsUtilService = gpsUtilService;
        this.rewardsService = rewardsService;

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(tourGuideService, this);
        addShutDownHook();
    }

    public VisitedLocation getUserLocation(User user) {
        return (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
    }

    public User getUser(String userName) {
        return userRepository.getInternalUserMap().get(userName);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.getInternalUserMap().values());
    }

    public void addUser(User user) {
        System.out.println("user = " + user);
        System.out.println("repo = " + userRepository.getInternalUserMap());
        if (!userRepository.getInternalUserMap().containsKey(user.getUserName())) {
            userRepository.addUserToInternalUserMap(user.getUserName(), user); //internalUserMap.put(user.getUserName(), user);
        }
    }

    public VisitedLocation trackUserLocation(User user) {
        VisitedLocation visitedLocation = gpsUtilService.getUserLocation(user);
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }

    public Map<String, LocationDTO> allCurrentLocations() {
        Map<String, LocationDTO> usersLocation = new TreeMap<>();
        List<User> users = getAllUsers();
        users.parallelStream().forEach(user -> {
            VisitedLocation lastVisitedLocation = user.getVisitedLocations().get(user.getVisitedLocations().size()-1);
            Location lastLocation = lastVisitedLocation.location;
            usersLocation.put(user.getUserId().toString(), new LocationDTO(lastLocation.longitude, lastLocation.latitude));
        });

        return usersLocation;
    }
    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    /**********************************************************************************
     * Methods Below: For Internal Testing
     **********************************************************************************/

    public void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            userRepository.addUserToInternalUserMap(userName, user);//.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }
}
