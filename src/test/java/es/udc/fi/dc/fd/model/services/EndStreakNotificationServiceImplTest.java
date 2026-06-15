package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.entities.NotificationEndStreakDao;
import es.udc.fi.dc.fd.model.entities.NotificationEndStreak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EndStreakNotificationServiceImplTest {

    private NotificationEndStreakDao dao;
    private EndStreakNotificationServiceImpl service;

    @BeforeEach
    public void setup() {
        dao = mock(NotificationEndStreakDao.class);
        service = new EndStreakNotificationServiceImpl(dao);
    }

    // ======================================================
    // TEST 1 — obtener notificaciones de un usuario
    // ======================================================
    @Test
    public void testGetNotificationsForUser() {

        NotificationEndStreak n1 = new NotificationEndStreak();
        NotificationEndStreak n2 = new NotificationEndStreak();
        List<NotificationEndStreak> list = List.of(n1, n2);

        when(dao.findByUserIdOrderByFechaCreacionDesc(10L))
                .thenReturn(list);

        List<NotificationEndStreak> result = service.getNotificationsForUser(10L);

        assertEquals(2, result.size());
        verify(dao).findByUserIdOrderByFechaCreacionDesc(10L);
    }

    // ======================================================
    // TEST 2 — contar no leídas
    // ======================================================
    @Test
    public void testCountUnreadForUser() {

        when(dao.countUnreadByUserId(15L))
                .thenReturn(4L);

        long result = service.countUnreadForUser(15L);

        assertEquals(4L, result);
        verify(dao).countUnreadByUserId(15L);
    }

    // ======================================================
    // TEST 3 — marcar como leída
    // ======================================================
    @Test
    public void testMarkAsRead() {

        NotificationEndStreak notif = new NotificationEndStreak();
        notif.setId(99L);
        notif.setLeido(false);

        when(dao.findById(99L)).thenReturn(Optional.of(notif));

        service.markAsRead(99L);

        assertTrue(notif.isLeido());
        verify(dao).save(notif);
    }
}
