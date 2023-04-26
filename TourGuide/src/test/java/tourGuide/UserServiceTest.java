package tourGuide;

import gpsUtil.GpsUtil;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import rewardCentral.RewardCentral;
import tourGuide.repository.UserRepository;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;
import tourGuide.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final GpsUtil gpsUtil = new GpsUtil();
    private final RewardCentral rewardsCentral = new RewardCentral();
    @InjectMocks
    public GpsUtilService gpsUtilService = new GpsUtilService(gpsUtil);

    @InjectMocks
    public UserService userService = new UserService(gpsUtilService, new RewardsService(gpsUtilService, rewardsCentral));
    //public UserService userService;

    @Mock
    public UserRepository userRepository;

    @BeforeEach
    public void setup() throws Exception{
//        gpsUtilService = new GpsUtilService(gpsUtil);
//        userService = new UserService(gpsUtilService, new RewardsService(gpsUtilService, rewardsCentral));
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void TestAddUser() {
        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        userService.addUser(user);
        userService.addUser(user2);

        Map<String, User> UserMap = new HashMap<>();
        UserMap.put(user.getUserName(), user);
        UserMap.put(user2.getUserName(), user2);

        //WHEN
        when(userRepository.getInternalUserMap()).thenReturn(UserMap);

        //THEN
        assertTrue(userService.getAllUsers().contains(user));
        assertTrue(userService.getAllUsers().contains(user2));
    }
}
