package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.entities.User;
import java.util.List;

public interface FollowService {

    void start_following(Long followerId, Long coachId);

    void stop_following(Long followerId, Long coachId);

    boolean alreadyFollowing(Long followerId, Long coachId);

    List<User> getFollowingList(Long followerId);

    List<User> getFollowersList(Long coachId);

    List<User> allFollowed(Long userId);
}
