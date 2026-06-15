package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.entities.FollowerNotification;
import es.udc.fi.dc.fd.model.entities.FollowerNotificationDao;
import es.udc.fi.dc.fd.model.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de notificaciones de seguidores
 */
@Service
@Transactional
public class FollowerNotificationServiceImpl implements FollowerNotificationService {
    
    @Autowired
    private FollowerNotificationDao followerNotificationDao;
    
    @Override
    public FollowerNotification createFollowerNotification(User coach, User follower) {
        FollowerNotification notification = new FollowerNotification(coach, follower);
        return followerNotificationDao.save(notification);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FollowerNotification> getNotificationsForCoach(Long coachId) {
        return followerNotificationDao.findByCoachIdOrderByCreatedAtDesc(coachId);
    }
    
    @Override
    public void markAsRead(Long notificationId) {
        FollowerNotification notification = followerNotificationDao.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));
        notification.setRead(true);
        followerNotificationDao.save(notification);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUnreadForCoach(Long coachId) {
        return followerNotificationDao.countUnreadByCoachId(coachId);
    }
}
