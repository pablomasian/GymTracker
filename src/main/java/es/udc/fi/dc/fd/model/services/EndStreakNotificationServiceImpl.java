package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.entities.NotificationEndStreak;
import es.udc.fi.dc.fd.model.entities.NotificationEndStreakDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EndStreakNotificationServiceImpl implements EndStreakNotificationService {

    private final NotificationEndStreakDao notificationEndStreakDao;

    public EndStreakNotificationServiceImpl(NotificationEndStreakDao notificationEndStreakDao) {
        this.notificationEndStreakDao = notificationEndStreakDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationEndStreak> getNotificationsForUser(Long userId) {
        return notificationEndStreakDao.findByUserIdOrderByFechaCreacionDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadForUser(Long userId) {
        return notificationEndStreakDao.countUnreadByUserId(userId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        NotificationEndStreak notif = notificationEndStreakDao.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notificación de fin de racha no encontrada"));

        notif.setLeido(true);
        notificationEndStreakDao.save(notif);
    }
}
