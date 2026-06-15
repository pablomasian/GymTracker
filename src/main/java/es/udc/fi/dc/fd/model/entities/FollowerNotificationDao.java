package es.udc.fi.dc.fd.model.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para notificaciones de nuevos seguidores
 */
@Repository
public interface FollowerNotificationDao extends JpaRepository<FollowerNotification, Long> {
    
    /**
     * Encuentra todas las notificaciones para un coach, ordenadas por fecha descendente
     */
    @Query("SELECT fn FROM FollowerNotification fn WHERE fn.coach.id = :coachId ORDER BY fn.createdAt DESC")
    List<FollowerNotification> findByCoachIdOrderByCreatedAtDesc(Long coachId);
    
    /**
     * Cuenta las notificaciones no leídas para un coach
     */
    @Query("SELECT COUNT(fn) FROM FollowerNotification fn WHERE fn.coach.id = :coachId AND fn.isRead = false")
    long countUnreadByCoachId(Long coachId);
}
