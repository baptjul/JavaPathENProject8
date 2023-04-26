package tourGuide.repository;

import org.springframework.stereotype.Repository;
import tourGuide.user.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {
    public Map<String, User> internalUserMap = new HashMap<>();

    public Map<String, User> getInternalUserMap() {
        return internalUserMap;
    }

    public void addUserToInternalUserMap(String key, User user) {
        internalUserMap.put(key, user);
    }
}
