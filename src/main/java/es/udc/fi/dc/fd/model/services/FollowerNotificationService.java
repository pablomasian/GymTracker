package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.entities.FollowerNotification;
import es.udc.fi.dc.fd.model.entities.User;

import java.util.List;

/**
 * Servicio para gestionar notificaciones de nuevos seguidores
 */
public interface FollowerNotificationService {
    
    /**
     * Crea una notificación cuando alguien sigue a un coach
     */
    FollowerNotification createFollowerNotification(User coach, User follower);
    
    /**
     * Obtiene todas las notificaciones de un coach
     */
    List<FollowerNotification> getNotificationsForCoach(Long coachId);
    
    /**
     * Marca una notificación como leída
     */
    void markAsRead(Long notificationId);
    
    /**
     * Cuenta las notificaciones no leídas de un coach
     */
    long countUnreadForCoach(Long coachId);
}
