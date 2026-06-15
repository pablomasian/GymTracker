package es.udc.fi.dc.fd.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.udc.fi.dc.fd.model.entities.Follow;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class FollowServiceImpl implements FollowService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private FollowDao followDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private FollowerNotificationService followerNotificationService;


    @Override
    public void start_following(Long followerId, Long coachId) {

        User follower = userDao.getReferenceById(followerId);
        User coach = userDao.getReferenceById(coachId);

        if (followDao.existsByFollowerIdAndCoachId(followerId, coachId)) {
            return;
        }

        Follow follow = new Follow(follower, coach);
        followDao.save(follow);

        followerNotificationService.createFollowerNotification(coach, follower);
    }


    @Override
    public void stop_following(Long followerId, Long coachId) {

        User follower = userDao.findById(followerId).orElse(null);
        User coach = userDao.findById(coachId).orElse(null);

        if (follower == null || coach == null) {
            return;
        }

        followDao.findByFollowerAndCoach(follower, coach)
                 .ifPresent(followDao::delete);
    }

    @Override
    public boolean alreadyFollowing(Long followerId, Long coachId) {
        return followDao.existsByFollowerIdAndCoachId(followerId, coachId);
    }

    @Override
    public List<User> getFollowingList(Long followerId) {
        return followDao.findCoachesByFollowerId(followerId);
    }

    @Override
    public List<User> getFollowersList(Long coachId) {
        return followDao.findFollowersByCoachId(coachId);
    }

    @Override
    public List<User> allFollowed(Long userId) {
        return entityManager.createQuery(
                "SELECT f.coach FROM Follow f WHERE f.follower.id = :userId",
                User.class
        )
        .setParameter("userId", userId)
        .getResultList();
    }
}
