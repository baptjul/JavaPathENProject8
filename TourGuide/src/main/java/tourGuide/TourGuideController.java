package tourGuide;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import gpsUtil.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.DTO.LocationDTO;
import tourGuide.DTO.NearbyAttractionDto;
import tourGuide.service.GpsUtilService;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;
import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;
    @Autowired
    GpsUtilService gpsUtilService;
    @Autowired
    UserService userService;

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation")
    @ResponseBody
    public Location getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return visitedLocation.location;
    }

    @RequestMapping("/getNearbyAttractions")
    @ResponseBody
    public CompletableFuture<NearbyAttractionDto> getNearbyAttractions(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return tourGuideService.getNearByAttractions(visitedLocation, getUser(userName));
    }
    
    @RequestMapping("/getRewards")
    @ResponseBody
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return userService.getUserRewards(getUser(userName));
    }
    
    @RequestMapping("/getAllCurrentLocations")
    @ResponseBody
    public Map<String, LocationDTO> getAllCurrentLocations() {
    	return userService.allCurrentLocations();
    }
    
    @RequestMapping("/getTripDeals")
    @ResponseBody
    public List<Provider> getTripDeals(@RequestParam String userName) {
        return tourGuideService.getTripDeals(getUser(userName));
    }

    @RequestMapping("/preferences")
    public UserPreferences updateUserPreferences(@RequestParam  String userName, @RequestBody UserPreferences userPreferences) {
        return userService.updateUserPreferences(userName, userPreferences);
    }
    
    private User getUser(String userName) {
    	return userService.getUser(userName);
    }
}