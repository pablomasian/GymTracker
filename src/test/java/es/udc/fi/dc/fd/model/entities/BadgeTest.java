package es.udc.fi.dc.fd.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import es.udc.fi.dc.fd.model.entities.Badge.BadgeType;

public class BadgeTest {

    @Test
    void testDefaultConstructor() {
        Badge badge = new Badge();
        assertEquals(0, badge.getId());
    }

    @Test
    void testConstructorWithHundred() {
        User user = new User("test", "password", "Test", "User", "testuser");
        Badge badge = new Badge(user, LocalDateTime.now(), BadgeType.HUNDRED);

        assertEquals(user, badge.getUser());
        assertEquals(BadgeType.HUNDRED, badge.getType());
        assertEquals("/img/badges/hundred.png", badge.getIconUrl());
        assertEquals("This user has completed a set using a weight of 100kg or more", badge.getDescription());
    }

    @Test
    void testConstructorWithFiftyWorkouts() {
        User user = new User("test", "password", "Test", "User", "testuser");
        Badge badge = new Badge(user, LocalDateTime.now(), BadgeType.FIFTY_WORKOUTS);

        assertEquals(BadgeType.FIFTY_WORKOUTS, badge.getType());
        assertEquals("/img/badges/fifty_workouts.png", badge.getIconUrl());
        assertEquals("This user has completed 50 workouts", badge.getDescription());
    }

    @Test
    void testConstructorWithEarlyBird() {
        User user = new User("test", "password", "Test", "User", "testuser");
        Badge badge = new Badge(user, LocalDateTime.now(), BadgeType.EARLY_BIRD);

        assertEquals(BadgeType.EARLY_BIRD, badge.getType());
        assertEquals("/img/badges/early_bird.png", badge.getIconUrl());
        assertEquals("This user has trained before 7:00 AM", badge.getDescription());
    }

    @Test
    void testConstructorWithVolumeKing() {
        User user = new User("test", "password", "Test", "User", "testuser");
        Badge badge = new Badge(user, LocalDateTime.now(), BadgeType.VOLUME_KING);

        assertEquals(BadgeType.VOLUME_KING, badge.getType());
        assertEquals("/img/badges/volume_king.png", badge.getIconUrl());
        assertEquals("This user has completed 200 or more reps in a single workout", badge.getDescription());
    }

    @Test
    void testConstructorWithConsistencyChampion() {
        User user = new User("test", "password", "Test", "User", "testuser");
        Badge badge = new Badge(user, LocalDateTime.now(), BadgeType.CONSISTENCY_CHAMPION);

        assertEquals(BadgeType.CONSISTENCY_CHAMPION, badge.getType());
        assertEquals("/img/badges/consistency_champion.png", badge.getIconUrl());
        assertEquals("This user has trained for 7 consecutive days", badge.getDescription());
    }

    @Test
    void testConstructorWithOther() {
        User user = new User("test", "password", "Test", "User", "testuser");
        Badge badge = new Badge(user, LocalDateTime.now(), BadgeType.OTHER);

        assertEquals(BadgeType.OTHER, badge.getType());
        assertEquals("/img/badges/other.png", badge.getIconUrl());
    }

    @Test
    void testSettersAndGetters() {
        Badge badge = new Badge();
        User user = new User("test", "password", "Test", "User", "testuser");
        LocalDateTime now = LocalDateTime.now();

        badge.setId(5L);
        badge.setUser(user);
        badge.setDate(now);
        badge.setType(BadgeType.HUNDRED);
        badge.setDescription("Custom description");
        badge.setIconUrl("/custom/icon.png");

        assertEquals(5L, badge.getId());
        assertEquals(user, badge.getUser());
        assertEquals(now, badge.getDate());
        assertEquals(BadgeType.HUNDRED, badge.getType());
        assertEquals("Custom description", badge.getDescription());
        assertEquals("/custom/icon.png", badge.getIconUrl());
    }
}
