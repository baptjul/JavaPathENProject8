package tourGuide.DTO;

import java.util.List;

public class NearbyAttractionDto {

    private List<String> attractionNames;
    private List<LocationDTO> attractionLocation;
    private LocationDTO userLocation;
    private List<Double> distances;
    private List<Integer> rewardPoints;

    public NearbyAttractionDto(List<String> attractionNames, List<LocationDTO> attractionLatLongs, LocationDTO userLatLong, List<Double> distances, List<Integer> rewardPoints) {
        this.attractionNames = attractionNames;
        this.attractionLocation = attractionLatLongs;
        this.userLocation = userLatLong;
        this.distances = distances;
        this.rewardPoints = rewardPoints;
    }

    public List<String> getAttractionNames() {
        return attractionNames;
    }

    public void setAttractionNames(List<String> attractionNames) {
        this.attractionNames = attractionNames;
    }

    public List<LocationDTO> getAttractionLocation() {
        return attractionLocation;
    }

    public void setAttractionLocation(List<LocationDTO> attractionLocation) {
        this.attractionLocation = attractionLocation;
    }

    public LocationDTO getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(LocationDTO userLocation) {
        this.userLocation = userLocation;
    }

    public List<Double> getDistances() {
        return distances;
    }

    public void setDistances(List<Double> distances) {
        this.distances = distances;
    }

    public List<Integer> getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(List<Integer> rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
