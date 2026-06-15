package es.udc.fi.dc.fd.rest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import es.udc.fi.dc.fd.model.entities.NotificationDao;
import es.udc.fi.dc.fd.model.services.EndStreakNotificationService;
import es.udc.fi.dc.fd.model.services.UserService;
import java.util.Collections;
import es.udc.fi.dc.fd.model.entities.User;

@WebMvcTest(NotificationsController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationsControllerWebTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private NotificationDao notificationDao;

    @MockBean
    private EndStreakNotificationService endStreakNotificationService;

    @MockBean
    private UserService userService;

    @MockBean
    private es.udc.fi.dc.fd.rest.common.JwtGenerator jwtGenerator;

    @MockBean
    private es.udc.fi.dc.fd.model.services.NotificationsFollowedCoachService notificationsFollowedCoachService;

    @MockBean
    private es.udc.fi.dc.fd.model.services.FollowerNotificationService followerNotificationService;

    @MockBean
    private es.udc.fi.dc.fd.model.services.SocialService socialService;

    @MockBean
    private es.udc.fi.dc.fd.model.entities.LikeDao likeDao;

    @MockBean
    private es.udc.fi.dc.fd.model.entities.CommentDao commentDao;

    @Test
    void getNotifications_whenNoNotifications_returnsOkEmpty() throws Exception {
        when(userService.loginFromId(anyLong())).thenReturn(new User());
        when(notificationDao.findByRecipientOrderByCreatedAtDesc(any())).thenReturn(Collections.emptyList());

    mvc.perform(get("/api/notifications").requestAttr("userId", 1L)).andExpect(status().isOk());

        verify(userService, times(1)).loginFromId(anyLong());
        verify(notificationDao, times(1)).findByRecipientOrderByCreatedAtDesc(any());
    }

}
