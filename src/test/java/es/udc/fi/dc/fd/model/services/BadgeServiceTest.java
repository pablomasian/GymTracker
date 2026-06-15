package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.entities.Badge;
import es.udc.fi.dc.fd.model.entities.Badge.BadgeType;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BadgeServiceTest {

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private UserService userService;

    private User testUser;

    private User createUser(String base) throws DuplicateInstanceException {
        User user = TestDataFactory.newUser(base);
        userService.signUp(user);
        return user;
    }

    @Before
    public void setUp() throws DuplicateInstanceException {
        testUser = createUser("badgeUser");
    }

    @Test
    public void testGiveBadge_Hundred() {
        badgeService.giveBadge(testUser.getId(), BadgeType.HUNDRED);

        List<Badge> badges = badgeService.getBadgesByUser(testUser.getId());
        assertEquals(1, badges.size());
        Badge badge = badges.get(0);
        assertEquals(BadgeType.HUNDRED, badge.getType());
        assertEquals(testUser.getId(), badge.getUser().getId());
        assertTrue(badge.getDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    public void testGiveBadge_Other() {
        badgeService.giveBadge(testUser.getId(), BadgeType.OTHER);

        List<Badge> badges = badgeService.getBadgesByUser(testUser.getId());
        assertEquals(1, badges.size());
        Badge badge = badges.get(0);
        assertEquals(BadgeType.OTHER, badge.getType());
        assertEquals(testUser.getId(), badge.getUser().getId());
    }

    @Test
    public void testGetBadgesByUser_Multiple() {
        badgeService.giveBadge(testUser.getId(), BadgeType.HUNDRED);
        badgeService.giveBadge(testUser.getId(), BadgeType.OTHER);

        List<Badge> badges = badgeService.getBadgesByUser(testUser.getId());
        assertEquals(2, badges.size());
        assertTrue(badges.stream().anyMatch(b -> b.getType() == BadgeType.HUNDRED));
        assertTrue(badges.stream().anyMatch(b -> b.getType() == BadgeType.OTHER));
    }

    @Test
    public void testHasBadge() {
        assertTrue(!badgeService.hasBadge(testUser.getId(), BadgeType.HUNDRED));
        badgeService.giveBadge(testUser.getId(), BadgeType.HUNDRED);
        assertTrue(badgeService.hasBadge(testUser.getId(), BadgeType.HUNDRED));
    }
    
    @Test
    public void testGiveBadge_FiftyWorkouts() {
        badgeService.giveBadge(testUser.getId(), BadgeType.FIFTY_WORKOUTS);

        List<Badge> badges = badgeService.getBadgesByUser(testUser.getId());
        assertEquals(1, badges.size());
        Badge badge = badges.get(0);
        assertEquals(BadgeType.FIFTY_WORKOUTS, badge.getType());
        assertEquals(testUser.getId(), badge.getUser().getId());
        assertTrue(badge.getDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(badge.getDescription().contains("50 workouts"));
    }

    @Test
    public void testGiveBadge_EarlyBird() {
        badgeService.giveBadge(testUser.getId(), BadgeType.EARLY_BIRD);

        List<Badge> badges = badgeService.getBadgesByUser(testUser.getId());
        assertEquals(1, badges.size());
        Badge badge = badges.get(0);
        assertEquals(BadgeType.EARLY_BIRD, badge.getType());
        assertEquals(testUser.getId(), badge.getUser().getId());
        assertTrue(badge.getDescription().contains("before 7:00 AM"));
    }

    @Test
    public void testGiveBadge_VolumeKing() {
        badgeService.giveBadge(testUser.getId(), BadgeType.VOLUME_KING);

        List<Badge> badges = badgeService.getBadgesByUser(testUser.getId());
        assertEquals(1, badges.size());
        Badge badge = badges.get(0);
        assertEquals(BadgeType.VOLUME_KING, badge.getType());
        assertEquals(testUser.getId(), badge.getUser().getId());
        assertTrue(badge.getDescription().contains("200 or more reps"));
    }

    @Test
    public void testGiveBadge_ConsistencyChampion() {
        badgeService.giveBadge(testUser.getId(), BadgeType.CONSISTENCY_CHAMPION);

        List<Badge> badges = badgeService.getBadgesByUser(testUser.getId());
        assertEquals(1, badges.size());
        Badge badge = badges.get(0);
        assertEquals(BadgeType.CONSISTENCY_CHAMPION, badge.getType());
        assertEquals(testUser.getId(), badge.getUser().getId());
        assertTrue(badge.getDescription().contains("7 consecutive days"));
    }

    @Test
    public void testGetBadgesByUser_AllNewBadges() {
        badgeService.giveBadge(testUser.getId(), BadgeType.FIFTY_WORKOUTS);
        badgeService.giveBadge(testUser.getId(), BadgeType.EARLY_BIRD);
        badgeService.giveBadge(testUser.getId(), BadgeType.VOLUME_KING);
        badgeService.giveBadge(testUser.getId(), BadgeType.CONSISTENCY_CHAMPION);

        List<Badge> badges = badgeService.getBadgesByUser(testUser.getId());
        assertEquals(4, badges.size());
        assertTrue(badges.stream().anyMatch(b -> b.getType() == BadgeType.FIFTY_WORKOUTS));
        assertTrue(badges.stream().anyMatch(b -> b.getType() == BadgeType.EARLY_BIRD));
        assertTrue(badges.stream().anyMatch(b -> b.getType() == BadgeType.VOLUME_KING));
        assertTrue(badges.stream().anyMatch(b -> b.getType() == BadgeType.CONSISTENCY_CHAMPION));
    }

    @Test
    public void testHasBadge_NewBadges() {
        assertTrue(!badgeService.hasBadge(testUser.getId(), BadgeType.FIFTY_WORKOUTS));
        assertTrue(!badgeService.hasBadge(testUser.getId(), BadgeType.EARLY_BIRD));
        assertTrue(!badgeService.hasBadge(testUser.getId(), BadgeType.VOLUME_KING));
        assertTrue(!badgeService.hasBadge(testUser.getId(), BadgeType.CONSISTENCY_CHAMPION));
        
        badgeService.giveBadge(testUser.getId(), BadgeType.FIFTY_WORKOUTS);
        badgeService.giveBadge(testUser.getId(), BadgeType.EARLY_BIRD);
        
        assertTrue(badgeService.hasBadge(testUser.getId(), BadgeType.FIFTY_WORKOUTS));
        assertTrue(badgeService.hasBadge(testUser.getId(), BadgeType.EARLY_BIRD));
        assertTrue(!badgeService.hasBadge(testUser.getId(), BadgeType.VOLUME_KING));
        assertTrue(!badgeService.hasBadge(testUser.getId(), BadgeType.CONSISTENCY_CHAMPION));
    }
}
