package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.entities.NotificationEndStreak;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class StreakServiceImplTest {

    private StreakServiceImpl streakService;
    private UserDao userDao;
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {
        userDao = mock(UserDao.class);
        entityManager = mock(EntityManager.class);
        streakService = new StreakServiceImpl(userDao);

        // Inyectamos EntityManager manualmente vía reflexión
        try {
            var field = StreakServiceImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(streakService, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------- TEST registrarEntrenamiento ---------- //

    @Test
    public void testRegistrarEntrenamiento_PrimeraVez() {
        User u = new User();
        u.setId(1L);

        when(userDao.getById(1L)).thenReturn(u);

        streakService.registrarEntrenamiento(1L);

        assertEquals(1, u.getStreakCount());
        assertNotNull(u.getNextStreakDeadline());
        verify(entityManager).merge(u);
    }

    @Test
    public void testRegistrarEntrenamiento_EntrenoAyer() {
        User u = new User();
        u.setId(1L);
        u.setStreakCount(3);
        u.setLastTrainingDate(LocalDate.now().minusDays(1));

        when(userDao.getById(1L)).thenReturn(u);

        streakService.registrarEntrenamiento(1L);

        assertEquals(4, u.getStreakCount());
        verify(entityManager).merge(u);
    }

    @Test
    public void testRegistrarEntrenamiento_RachaRota() {
        User u = new User();
        u.setId(1L);
        u.setStreakCount(5);
        u.setLastTrainingDate(LocalDate.now().minusDays(5));

        when(userDao.getById(1L)).thenReturn(u);

        streakService.registrarEntrenamiento(1L);

        assertEquals(1, u.getStreakCount());
        verify(entityManager).merge(u);
    }

    // ---------- TEST generarAvisosFinDeRacha ---------- //

    @Test
    public void testGenerarAvisosFinDeRacha_CreaNotificacion() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusHours(2);

        User u = new User();
        u.setId(1L);
        u.setStreakCount(4);
        u.setNextStreakDeadline(deadline);

        // Mock query de usuarios en ventana
        TypedQuery<User> mockQUsers = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(mockQUsers);
        when(mockQUsers.setParameter(anyString(), any())).thenReturn(mockQUsers);
        when(mockQUsers.getResultList()).thenReturn(List.of(u));

        // Mock query duplicados
        TypedQuery<NotificationEndStreak> mockQNotif = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(NotificationEndStreak.class)))
                .thenReturn(mockQNotif);
        when(mockQNotif.setParameter(anyString(), any())).thenReturn(mockQNotif);
        when(mockQNotif.getResultList()).thenReturn(List.of()); // no duplicados

        streakService.generarAvisosFinDeRacha();

        verify(entityManager).persist(any(NotificationEndStreak.class));
    }

    @Test
    public void testGenerarAvisosFinDeRacha_RompeRachaExpirada() {

        User u = new User();
        u.setId(1L);
        u.setStreakCount(5);
        u.setLastTrainingDate(LocalDate.now().minusDays(3));
        u.setNextStreakDeadline(LocalDateTime.now().minusHours(2));

        // -------- Query 1: usuarios dentro del rango (vacío) -------
        TypedQuery<User> mockQWindow = mock(TypedQuery.class);
        when(entityManager.createQuery(
                contains("nextStreakDeadline BETWEEN"), // MUCHÍSIMO más fiable
                eq(User.class)))
                .thenReturn(mockQWindow);
        when(mockQWindow.setParameter(anyString(), any())).thenReturn(mockQWindow);
        when(mockQWindow.getResultList()).thenReturn(List.of());

        // -------- Query 2: rachas expiradas -------
        TypedQuery<User> mockQExpired = mock(TypedQuery.class);
        when(entityManager.createQuery(
                contains("nextStreakDeadline <"),
                eq(User.class)))
                .thenReturn(mockQExpired);
        when(mockQExpired.setParameter(anyString(), any())).thenReturn(mockQExpired);
        when(mockQExpired.getResultList()).thenReturn(List.of(u));

        // -------- Query 3: duplicados -------
        TypedQuery<NotificationEndStreak> mockNotif = mock(TypedQuery.class);
        when(entityManager.createQuery(
                contains("NotificationEndStreak"),
                eq(NotificationEndStreak.class)))
                .thenReturn(mockNotif);
        when(mockNotif.setParameter(anyString(), any())).thenReturn(mockNotif);
        when(mockNotif.getResultList()).thenReturn(List.of());

        streakService.generarAvisosFinDeRacha();

        assertEquals(0, u.getStreakCount());
        assertNull(u.getNextStreakDeadline());
        verify(entityManager).merge(u);
    }

}
