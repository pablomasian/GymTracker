package es.udc.fi.dc.fd.model.entities;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationFollowedCoachDao extends JpaRepository<NotificationFollowedCoach, Long> {

    @Query("SELECT n FROM NotificationFollowedCoach n WHERE n.user.id = :userId ORDER BY n.id DESC")
    List<NotificationFollowedCoach> getNotificationsFollowedCoachByUser(Long userId);

    @Query("SELECT COUNT(fn) FROM NotificationFollowedCoach fn WHERE fn.user.id = :userId AND fn.isRead = false")
    long countUnreadByUserId(Long userId);
}
