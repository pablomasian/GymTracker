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
import es.udc.fi.dc.fd.model.entities.NotificationFollowedCoach;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.User.RoleType;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class NotificationsFollowedCoachServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @Autowired
    private NotificationsFollowedCoachService notificationService;
    
    private User coach;
    private User follower1;
    private User follower2;

    @Before
    public void setUp() throws DuplicateInstanceException {
        User c = TestDataFactory.newUser("coach");
        c.setRole(RoleType.COACH);
        userService.signUp(c);
        coach = c;

        User f1 = TestDataFactory.newUser("follower1");
        userService.signUp(f1);
        follower1 = f1;

        User f2 = TestDataFactory.newUser("follower2");
        userService.signUp(f2);
        follower2 = f2;
        
        followService.start_following(follower1.getId(), coach.getId());
        followService.start_following(follower2.getId(), coach.getId());
    }

    @Test
    public void testNotifyFollowers() {
        notificationService.notifyFollowers(coach.getId(), "New Routine");

        List<NotificationFollowedCoach> notifications1 = notificationService.getMyNotifications(follower1.getId());
        assertEquals(1, notifications1.size());
        assertEquals("New Routine", notifications1.get(0).getRoutineName());

        List<NotificationFollowedCoach> notifications2 = notificationService.getMyNotifications(follower2.getId());
        assertEquals(1, notifications2.size());
        assertEquals("New Routine", notifications2.get(0).getRoutineName());
    }

    @Test
    public void testGetAndMarkNotifications() {
        notificationService.notifyFollowers(coach.getId(), "My Awesome Routine");
        
        List<NotificationFollowedCoach> notifications = notificationService.getMyNotifications(follower1.getId());
        assertEquals(1, notificationService.countUnread(follower1.getId()));
        
        notificationService.markAsRead(notifications.get(0).getId());
        
        assertEquals(0, notificationService.countUnread(follower1.getId()));
        assertTrue(notificationService.getMyNotifications(follower1.getId()).get(0).isRead());
    }
}