package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.entities.NotificationEndStreak;

import java.util.List;

public interface EndStreakNotificationService {

    /**
     * Obtiene todas las notificaciones de fin de racha de un usuario.
     */
    List<NotificationEndStreak> getNotificationsForUser(Long userId);

    /**
     * Cuenta las notificaciones de fin de racha no leídas de un usuario.
     */
    long countUnreadForUser(Long userId);

    /**
     * Marca una notificación de fin de racha como leída.
     */
    void markAsRead(Long notificationId);
}
