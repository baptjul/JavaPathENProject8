package tourGuide;

import gpsUtil.GpsUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GpsUtilService gpsUtilService;

    @InjectMocks
    private UserService userService;


    @BeforeEach
    public void setup() throws Exception{
        //userService.initializeInternalUsers();
    }

//    @Test
//    public void testAddUser() {
//        //GIVEN
//        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//
//        Map<String, User> UserMap = new HashMap<>();
//        //UserMap.put(user.getUserName(), user);
//        when(userRepository.getInternalUserMap()).thenReturn(UserMap);
//
//        //WHEN
//        userService.addUser(user);
//
//        //THEN
//        assertTrue(userService.getAllUsers().contains(user));
//        verify(userRepository, times(1)).addUserToInternalUserMap(anyString(), any(User.class));
//
//    }
}

