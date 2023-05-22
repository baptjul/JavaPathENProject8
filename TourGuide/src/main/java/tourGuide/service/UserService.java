package tourGuide.service;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.DTO.LocationDTO;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.UserRepository;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    //private final GpsUtil gpsUtil;
    private final UserRepository userRepository;
    private GpsUtilService gpsUtilService;

    public UserService(GpsUtilService gpsUtilService, UserRepository userRepository) {
        this.gpsUtilService = gpsUtilService;
        this.userRepository = userRepository;
    }

    public User getUser(String userName) {
        return userRepository.getInternalUserMap().get(userName);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.getInternalUserMap().values());
    }

    public void addUser(User user) {
        if (!userRepository.getInternalUserMap().containsKey(user.getUserName())) {
            userRepository.addUserToInternalUserMap(user.getUserName(), user);
        }
    }

    public UserPreferences updateUserPreferences(String userName, UserPreferences userPreferences) {
        User target = getUser(userName);
        if (target != null) {
            target.getUserPreferences().setAttractionProximity(userPreferences.getAttractionProximity());
            target.getUserPreferences().setCurrency(userPreferences.getCurrency());
            target.getUserPreferences().setLowerPricePoint(userPreferences.getLowerPricePoint());
            target.getUserPreferences().setHighPricePoint(userPreferences.getHighPricePoint());
            target.getUserPreferences().setTripDuration(userPreferences.getTripDuration());
            target.getUserPreferences().setTicketQuantity(userPreferences.getTicketQuantity());
            target.getUserPreferences().setNumberOfAdults(userPreferences.getNumberOfAdults());
            target.getUserPreferences().setNumberOfChildren(userPreferences.getNumberOfChildren());

            return target.getUserPreferences();
        } else {
            return null;
        }
    }

    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
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

    public void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(gpsUtilService.generateRandomLatitude(), gpsUtilService.generateRandomLongitude()), gpsUtilService.getRandomTime()));
        });
    }
}
