package es.udc.fi.dc.fd.rest.controllers;

import es.udc.fi.dc.fd.model.entities.NotificationEndStreak;
import es.udc.fi.dc.fd.model.services.EndStreakNotificationService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificationsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class NotificationsControllerStreakWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private EndStreakNotificationService endStreakNotificationService;
    @MockBean private es.udc.fi.dc.fd.model.entities.NotificationDao notificationDao;
    @MockBean private es.udc.fi.dc.fd.model.services.UserService userService;
    @MockBean private es.udc.fi.dc.fd.model.services.NotificationsFollowedCoachService notificationsFollowedCoachService;
    @MockBean private es.udc.fi.dc.fd.model.services.FollowerNotificationService followerNotificationService;
    @MockBean private es.udc.fi.dc.fd.model.services.SocialService socialService;
    @MockBean private es.udc.fi.dc.fd.model.entities.LikeDao likeDao;
    @MockBean private es.udc.fi.dc.fd.model.entities.CommentDao commentDao;

    @MockBean private es.udc.fi.dc.fd.rest.common.JwtFilter jwtFilter;
    @MockBean private es.udc.fi.dc.fd.rest.common.JwtGenerator jwtGenerator;

    @Test
    public void testGetStreakNotifications() throws Exception {

        NotificationEndStreak notif = new NotificationEndStreak();
        notif.setId(1L);
        notif.setDiasRacha(4);
        notif.setFechaCreacion(LocalDateTime.now());
        notif.setFechaLimite(LocalDateTime.now().plusHours(3));

        when(endStreakNotificationService.getNotificationsForUser(10L))
                .thenReturn(List.of(notif));

        when(userService.loginFromId(10L)).thenReturn(null);

        mockMvc.perform(get("/api/notifications/streak")
                .requestAttr("userId", 10L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetStreakUnreadCount() throws Exception {

        when(endStreakNotificationService.countUnreadForUser(7L)).thenReturn(5L);
        when(userService.loginFromId(7L)).thenReturn(null);

        mockMvc.perform(get("/api/notifications/streak/unread-count")
                .requestAttr("userId", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    public void testMarkStreakNotificationRead() throws Exception {

        mockMvc.perform(post("/api/notifications/streak/33/read"))
                .andExpect(status().isNoContent());

        verify(endStreakNotificationService).markAsRead(33L);
    }
}
