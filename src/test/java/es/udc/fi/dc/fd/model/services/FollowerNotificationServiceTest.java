package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.entities.FollowerNotification;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.User.RoleType;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class FollowerNotificationServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private FollowerNotificationService notificationService;

    private User coach;
    private User follower;

    @Before
    public void setUp() throws DuplicateInstanceException {
        // Generate unique usernames to prevent cross-class UNIQUE constraint collisions.
        User c = TestDataFactory.newUser("coach");
        c.setRole(RoleType.COACH);
        userService.signUp(c);
        coach = c;

        User f = TestDataFactory.newUser("follower");
        userService.signUp(f);
        follower = f;
    }

    @Test
    public void testCreateAndGetNotifications() {
        assertEquals(0, notificationService.getNotificationsForCoach(coach.getId()).size());

        notificationService.createFollowerNotification(coach, follower);

        List<FollowerNotification> notifications = notificationService.getNotificationsForCoach(coach.getId());
        assertEquals(1, notifications.size());
        assertEquals(follower.getId(), notifications.get(0).getFollower().getId());
    }

    @Test
    public void testMarkAsReadAndCountUnread() {
        FollowerNotification notification = notificationService.createFollowerNotification(coach, follower);
        assertEquals(1, notificationService.countUnreadForCoach(coach.getId()));

        notificationService.markAsRead(notification.getId());

        assertEquals(0, notificationService.countUnreadForCoach(coach.getId()));
        FollowerNotification readNotification = notificationService.getNotificationsForCoach(coach.getId()).get(0);
        assertTrue(readNotification.isRead());
    }
}