package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.Test;

import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;

public class LikeDtoTest {

    @Test
    public void testFullConstructor() {
        User liker = new User();
        liker.setId(2L);
        liker.setNombreUsuario("liker");

        User liked = new User();
        liked.setId(3L);
        liked.setNombreUsuario("liked");

        Routine routine = new Routine();
        routine.setName("R");

        WorkoutSession session = new WorkoutSession();
        session.setId(4L);
        session.setRoutine(routine);

        Instant now = Instant.now();

        LikeDto dto = new LikeDto(1L, liker, liked, session, now);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getLiker_id());
        assertEquals("liker", dto.getLiker_username());
        assertEquals(3L, dto.getLiked_id());
        assertEquals("liked", dto.getLiked_username());
        assertEquals(4L, dto.getSessionId());
        assertEquals("R", dto.getRoutineName());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    public void testSettersAndEmptyConstructor() {
        LikeDto dto = new LikeDto();

        dto.setLiker_id(20L);
        dto.setLiker_username("LikerUser");
        dto.setLiked_id(30L);
        dto.setLiked_username("LikedUser");
        dto.setSessionId(40L);
        dto.setRoutineName("RoutineTest");
        dto.setRead(true);
        
        assertNull(dto.getId());
        assertEquals(20L, dto.getLiker_id());
        assertEquals("LikerUser", dto.getLiker_username());
        assertEquals(30L, dto.getLiked_id());
        assertEquals("LikedUser", dto.getLiked_username());
        assertEquals(40L, dto.getSessionId());
        assertEquals("RoutineTest", dto.getRoutineName());
        assertTrue(dto.isRead());
    }
}