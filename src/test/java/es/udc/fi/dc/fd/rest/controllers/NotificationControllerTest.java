package es.udc.fi.dc.fd.rest.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Disabled;


import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.entities.Notification;
import es.udc.fi.dc.fd.model.entities.NotificationDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.services.UserService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationDao notificationDao;

    private User user1;
    private Notification notification1;

    @BeforeEach
    void setUp() throws Exception {
        notificationDao.deleteAll();
        userService.signUp(new User("user1", "password", "User", "One", "user1"));
        user1 = userService.login("user1", "password");

        notification1 = new Notification(user1, "TEST_TYPE", "This is a test notification");
        notification1.setRead(false);
        notification1.setCreatedAt(Instant.now());
        notificationDao.save(notification1);
    }

    @Test
    void testMyNotifications() throws Exception {
        mockMvc.perform(get("/api/notifications")
                .requestAttr("userId", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].message").value("This is a test notification"));
    }

    @Test
    void testMarkAsRead() throws Exception {
        mockMvc.perform(post("/api/notifications/" + notification1.getId() + "/read")
                .requestAttr("userId", user1.getId()))
                .andExpect(status().isNoContent());

        Notification updatedNotification = notificationDao.findById(notification1.getId()).get();
        assertTrue(updatedNotification.isRead());
    }

    @Test
    void testMarkAsRead_PermissionDenied() throws Exception {
        User user2 = new User("user2", "password", "User", "Two", "user2");
        userService.signUp(user2);

        mockMvc.perform(post("/api/notifications/" + notification1.getId() + "/read")
                .requestAttr("userId", user2.getId()))
                .andExpect(status().isNoContent()); 

        Notification notification = notificationDao.findById(notification1.getId()).get();
        assertTrue(!notification.isRead()); 
    }
}