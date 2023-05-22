package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RewardsService {

    private final RewardCentral rewardsCentral;
    private GpsUtilService gpsUtilService;

    public RewardsService(GpsUtilService gpsUtilService, RewardCentral rewardCentral) {
        this.gpsUtilService = gpsUtilService;
        this.rewardsCentral = rewardCentral;
    }

    @Async
    public CompletableFuture<Void> calculateRewards(User user) {
        return CompletableFuture.runAsync(() -> {
            List<VisitedLocation> userLocations = user.getVisitedLocations();
            List<Attraction> attractions = gpsUtilService.getAttractions();

            for (VisitedLocation userLocation : userLocations ) {
                attractions.stream().filter(attraction -> gpsUtilService.nearAttraction(userLocation, attraction)).forEach(attraction -> {
                    if (user.getUserRewards().stream().noneMatch(r -> r.getAttraction().attractionName.equals(attraction.attractionName))) {
                        user.addUserReward(new UserReward(userLocation, attraction, getRewardPoints(attraction, user)));
                    }
                });
            }
        });
    }

    public CompletableFuture<Void> calculateMultipleRewards(List<User> users) {
        return CompletableFuture.runAsync(() -> {
            List<Attraction> attractions = gpsUtilService.getAttractions();
            users.parallelStream().forEach(user -> calculateUserRewards(user, attractions));
        });
    }

    public void calculateUserRewards(User user, List<Attraction> attractions) {
        List<VisitedLocation> userLocations = user.getVisitedLocations();
        System.out.println("user = " + user + ", attractions = " + attractions);
        for (VisitedLocation userLocation : userLocations) {
            attractions.stream().filter(attraction -> gpsUtilService.nearAttraction(userLocation, attraction)).forEach(attraction -> {
                if (user.getUserRewards().stream().noneMatch(r -> r.getAttraction().attractionName.equals(attraction.attractionName))) {
                    user.addUserReward(new UserReward(userLocation, attraction, getRewardPoints(attraction, user)));
                }
            });
        }
    }

    public int getRewardPoints(Attraction attraction, User user) {
        return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
    }
}
