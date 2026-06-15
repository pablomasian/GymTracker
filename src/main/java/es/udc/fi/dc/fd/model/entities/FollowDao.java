package es.udc.fi.dc.fd.model.entities;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowDao extends JpaRepository<Follow, Long> {

    boolean existsByFollowerIdAndCoachId(Long followerId, Long coachId);

    Optional<Follow> findByFollowerAndCoach(User follower, User coach);

    @Query("SELECT f FROM Follow f WHERE f.coach.id = :coachId")
    List<Follow> findByCoachId(Long coachId);

    @Query("SELECT f.coach FROM Follow f WHERE f.follower.id = :userId")
    List<User> findCoachesByFollowerId(Long userId);

    @Query("SELECT f.follower FROM Follow f WHERE f.coach.id = :coachId")
    List<User> findFollowersByCoachId(Long coachId);
}
