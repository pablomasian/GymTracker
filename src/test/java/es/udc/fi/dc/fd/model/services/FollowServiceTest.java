package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.FollowerNotificationDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.User.RoleType;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class FollowServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @Autowired
    private FollowDao followDao;

    @Autowired
    private FollowerNotificationDao followerNotificationDao;

    private User user;
    private User coach;

    @Before
    public void setUp() throws DuplicateInstanceException {
        followerNotificationDao.deleteAll();
        followDao.deleteAll();

        User u = TestDataFactory.newUser("user");
        userService.signUp(u);
        user = u;

        User c = TestDataFactory.newUser("coach");
        c.setRole(RoleType.COACH);
        userService.signUp(c);
        coach = c;
    }

    @Test
    public void testFollowAndUnfollow() {
        assertFalse(followService.alreadyFollowing(user.getId(), coach.getId()));

        followService.start_following(user.getId(), coach.getId());

        assertTrue(followService.alreadyFollowing(user.getId(), coach.getId()));
        assertEquals(1, followerNotificationDao.count());

        followService.stop_following(user.getId(), coach.getId());
        
        assertFalse(followService.alreadyFollowing(user.getId(), coach.getId()));
    }

    @Test
    public void testAlreadyFollowing() {
        assertFalse(followService.alreadyFollowing(user.getId(), coach.getId()));
        followService.start_following(user.getId(), coach.getId());
        assertTrue(followService.alreadyFollowing(user.getId(), coach.getId()));
    }
}