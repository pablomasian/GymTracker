package es.udc.fi.dc.fd.rest.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import es.udc.fi.dc.fd.model.entities.NotificationEndStreak;
import es.udc.fi.dc.fd.model.entities.NotificationEndStreakDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.User.RoleType;
import es.udc.fi.dc.fd.model.entities.UserDao;

@DataJpaTest
public class NotificationEndStreakDaoTest {

    @Autowired
    private NotificationEndStreakDao notificationEndStreakDao;

    @Autowired
    private UserDao userDao;

    /** Crea un usuario válido según tus constraints de BD */
    private User createValidUser(String username) {
        User u = new User();
        u.setUsername(username);
        u.setPassword("pass123");

        u.setNombreUsuario(username + "_name");
        u.setFirstName("Test");
        u.setLastName("User");
        u.setEmail(username + "@test.com");

        u.setRole(RoleType.USER);
        u.setBlocked(false);
        u.setPremium(false);

        return userDao.save(u);
    }

    @Test
    @Rollback
    public void testSaveAndFindNotification() {

        User u = createValidUser("testuser");

        NotificationEndStreak notif = new NotificationEndStreak();
        notif.setUser(u);
        notif.setDiasRacha(5);
        notif.setMensaje("Your streak is about to end!");
        notif.setFechaCreacion(LocalDateTime.now());
        notif.setFechaLimite(LocalDateTime.now().plusHours(1));

        notificationEndStreakDao.save(notif);

        NotificationEndStreak found = notificationEndStreakDao.findById(notif.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getDiasRacha()).isEqualTo(5);
        assertThat(found.getUser().getId()).isEqualTo(u.getId());
    }

    @Test
    @Rollback
    public void testFindUnreadByUser() {

        User u = createValidUser("user2");

        NotificationEndStreak n1 = new NotificationEndStreak();
        n1.setUser(u);
        n1.setDiasRacha(3);
        n1.setMensaje("A");
        n1.setFechaCreacion(LocalDateTime.now());
        n1.setFechaLimite(LocalDateTime.now());
        n1.setLeido(false);
        notificationEndStreakDao.save(n1);

        NotificationEndStreak n2 = new NotificationEndStreak();
        n2.setUser(u);
        n2.setDiasRacha(4);
        n2.setMensaje("B");
        n2.setFechaCreacion(LocalDateTime.now().plusSeconds(1));
        n2.setFechaLimite(LocalDateTime.now());
        n2.setLeido(true);
        notificationEndStreakDao.save(n2);

        List<NotificationEndStreak> result = notificationEndStreakDao.findByUserIdOrderByFechaCreacionDesc(u.getId());

        assertThat(result).hasSize(2);

        // Ahora sí, la más nueva SIEMPRE será la B
        assertThat(result.get(0).getMensaje()).isEqualTo("B");
        assertThat(result.get(1).getMensaje()).isEqualTo("A");
    }

    @Test
    @Rollback
    public void testMarkAsRead() {

        User u = createValidUser("user3");

        NotificationEndStreak notif = new NotificationEndStreak();
        notif.setUser(u);
        notif.setDiasRacha(2);
        notif.setMensaje("Test");
        notif.setFechaCreacion(LocalDateTime.now());
        notif.setFechaLimite(LocalDateTime.now());
        notif.setLeido(false);

        notificationEndStreakDao.save(notif);

        // Marcar como leída
        notif.setLeido(true);
        notificationEndStreakDao.save(notif);

        List<NotificationEndStreak> result = notificationEndStreakDao.findByUserIdOrderByFechaCreacionDesc(u.getId());

        // El DAO sigue devolviendo la notificación aunque esté leída
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isLeido()).isTrue();
    }
}
