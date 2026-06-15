package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.NotificationEndStreak;
import es.udc.fi.dc.fd.model.entities.UserDao;

import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class StreakServiceImpl implements StreakService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserDao userDao;

    public StreakServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional
    public void registrarEntrenamiento(Long userId) {

        User user = userDao.getById(userId);
        LocalDate today = LocalDate.now();

        if (user.getLastTrainingDate() == null) {
            // Primera vez que entrena
            user.setStreakCount(1);
        } else if (user.getLastTrainingDate().equals(today.minusDays(1))) {
            // Entrenó ayer → racha continua
            user.setStreakCount(user.getStreakCount() + 1);
        } else if (user.getLastTrainingDate().equals(today)) {
            // Ya entrenó hoy → no hacer nada
        } else {
            // No entrenó ayer → racha rota → empezamos otra vez
            user.setStreakCount(1);
        }

        user.setLastTrainingDate(today);
        user.setNextStreakDeadline(today.plusDays(1).atTime(23, 59, 59));

        entityManager.merge(user);
    }

    @Override
    @Transactional
    public void generarAvisosFinDeRacha() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limite = now.plusHours(4);

        // 1) Avisos: racha a punto de romperse
        String query = """
                    SELECT u FROM User u
                    WHERE u.nextStreakDeadline BETWEEN :now AND :limite
                """;

        var usuarios = entityManager.createQuery(query, User.class)
                .setParameter("now", now)
                .setParameter("limite", limite)
                .getResultList();

        for (User u : usuarios) {

            // Evitar duplicados
            boolean exists = !entityManager.createQuery("""
                        SELECT n FROM NotificationEndStreak n
                        WHERE n.usuario = :user
                        AND n.fechaLimite = :deadline
                        AND n.leido = false
                    """, NotificationEndStreak.class)
                    .setParameter("user", u)
                    .setParameter("deadline", u.getNextStreakDeadline())
                    .getResultList()
                    .isEmpty();

            if (!exists) {
                NotificationEndStreak notif = new NotificationEndStreak();
                notif.setUser(u);
                notif.setDiasRacha(u.getStreakCount());
                notif.setFechaLimite(u.getNextStreakDeadline());
                notif.setMensaje("Your streak is about to end!");
                entityManager.persist(notif);
            }
        }

        // 2) Romper rachas ya expiradas
        String expQuery = """
                    SELECT u FROM User u
                    WHERE u.nextStreakDeadline < :now
                    AND u.streakCount IS NOT NULL
                """;

        var expirados = entityManager.createQuery(expQuery, User.class)
                .setParameter("now", now)
                .getResultList();

        for (User u : expirados) {

            // Si entrenó hoy, no romper la racha
            if (u.getLastTrainingDate() != null &&
                    !u.getLastTrainingDate().isBefore(u.getNextStreakDeadline().toLocalDate())) {
                continue;
            }

            // Romper la racha
            u.setStreakCount(0);
            u.setNextStreakDeadline(null);

            entityManager.merge(u);
        }
    }

    @Transactional
    public void registrarEntrenamientoEnFecha(Long userId, LocalDate date) {

        User user = userDao.getById(userId);

        if (user.getLastTrainingDate() == null) {
            user.setStreakCount(1);
        } else if (user.getLastTrainingDate().equals(date.minusDays(1))) {
            user.setStreakCount(user.getStreakCount() + 1);
        } else if (user.getLastTrainingDate().equals(date)) {
            // mismo día → no hacer nada
        } else {
            user.setStreakCount(1);
        }

        user.setLastTrainingDate(date);
        user.setNextStreakDeadline(date.plusDays(1).atTime(23, 59, 59));

        entityManager.merge(user);
    }

}