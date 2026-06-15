package es.udc.fi.dc.fd.model.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FollowTest {

    @Test
    void testFollowConstructorAndGetters() {
        User follower = new User();
        follower.setId(1L);
        follower.setUsername("follower");
        
        User coach = new User();
        coach.setId(2L);
        coach.setUsername("coach");

        Follow follow = new Follow(follower, coach);

        assertNotNull(follow);
        assertEquals(follower, follow.getFollower());
        assertEquals(coach, follow.getCoach());
        assertEquals(1L, follow.getFollower().getId());
        assertEquals(2L, follow.getCoach().getId());
    }

    @Test
    void testFollowSetters() {
        Follow follow = new Follow();

        User follower = new User();
        follower.setId(3L);
        follower.setUsername("newFollower");
        
        User coach = new User();
        coach.setId(4L);
        coach.setUsername("newCoach");

        follow.setFollower(follower);
        follow.setCoach(coach);
        follow.setId(100L);

        assertEquals(follower, follow.getFollower());
        assertEquals(coach, follow.getCoach());
        assertEquals(100L, follow.getId());
    }

    @Test
    void testFollowDefaultConstructor() {
        Follow follow = new Follow();
        
        assertNotNull(follow);
        assertNull(follow.getId());
        assertNull(follow.getFollower());
        assertNull(follow.getCoach());
    }
}
