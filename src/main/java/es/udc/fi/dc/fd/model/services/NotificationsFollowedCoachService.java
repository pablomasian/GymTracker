package es.udc.fi.dc.fd.model.services;

import java.util.List;

import es.udc.fi.dc.fd.model.entities.NotificationFollowedCoach;

public interface NotificationsFollowedCoachService {
    List<NotificationFollowedCoach> getMyNotifications (Long userId);

    void notifyFollowers (Long coachId, String routineName);
    
    void markAsRead(Long notificationId);

    long countUnread(Long userId);
}
