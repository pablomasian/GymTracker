package es.udc.fi.dc.fd.model.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.udc.fi.dc.fd.model.entities.Follow;
import es.udc.fi.dc.fd.model.entities.NotificationFollowedCoach;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.FollowerNotification;
import es.udc.fi.dc.fd.model.entities.NotificationFollowedCoachDao;
import org.springframework.transaction.annotation.Transactional;

// Servicio: gestiona notificaciones cuando un coach seguido publica una rutina
@Service
@Transactional
public class NotificationsFollowedCoachServiceImpl implements NotificationsFollowedCoachService {

    @Autowired 
    private NotificationFollowedCoachDao notificationsFollowedCoachDao;

    @Autowired
    private FollowDao followDao;

    @Override
    // Devuelve las notificaciones para un usuario
    public List<NotificationFollowedCoach> getMyNotifications(Long userId){
        return notificationsFollowedCoachDao.getNotificationsFollowedCoachByUser(userId);
    }

    // Crea una notificación para cada seguidor del coach
    public void notifyFollowers(Long coachId, String routineName){
        // Obtener todos los seguidores del coach
        List<Follow> followers = followDao.findByCoachId(coachId);

        for (Follow f : followers) {
            User followerUser = f.getFollower();
            User coachUser = f.getCoach();

            NotificationFollowedCoach notif = new NotificationFollowedCoach();
            notif.setUser(followerUser);
            notif.setCoach(coachUser);
            notif.setRoutineName(routineName);
            notificationsFollowedCoachDao.save(notif);
        }
    }

    @Override
    public void markAsRead(Long notificationId) {
        NotificationFollowedCoach notification = notificationsFollowedCoachDao.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));
        notification.setRead(true);
        notificationsFollowedCoachDao.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(Long userId){
        return notificationsFollowedCoachDao.countUnreadByUserId(userId);
    }
}
