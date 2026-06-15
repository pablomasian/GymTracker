package es.udc.fi.dc.fd.model.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationEndStreakDao extends JpaRepository<NotificationEndStreak, Long> {

    /**
     * Notificaciones de fin de racha de un usuario, ordenadas por fecha creación desc.
     */
    @Query("""
        SELECT n FROM NotificationEndStreak n
        WHERE n.usuario.id = :userId
        ORDER BY n.fechaCreacion DESC
    """)
    List<NotificationEndStreak> findByUserIdOrderByFechaCreacionDesc(Long userId);

    /**
     * Cuenta las notificaciones no leídas de fin de racha de un usuario.
     */
    @Query("""
        SELECT COUNT(n) FROM NotificationEndStreak n
        WHERE n.usuario.id = :userId AND n.leido = false
    """)
    long countUnreadByUserId(Long userId);
}
