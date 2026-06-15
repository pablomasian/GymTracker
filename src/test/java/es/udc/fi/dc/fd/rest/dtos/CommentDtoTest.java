package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;

public class CommentDtoTest {

    @Test
    void testConstructorAndGetters() {
        User commenter = mock(User.class);
        when(commenter.getId()).thenReturn(1L);
        when(commenter.getNombreUsuario()).thenReturn("commenter_user");

        User commented = mock(User.class);
        when(commented.getId()).thenReturn(2L);
        when(commented.getNombreUsuario()).thenReturn("commented_user");

        Routine routine = mock(Routine.class);
        when(routine.getName()).thenReturn("Test Routine");

        WorkoutSession session = mock(WorkoutSession.class);
        when(session.getId()).thenReturn(10L);
        when(session.getRoutine()).thenReturn(routine);

        Instant now = Instant.now();

        CommentDto dto = new CommentDto(5L, session, commenter, commented, "Great workout!", now);

        assertEquals(5L, dto.getId());
        assertEquals(1L, dto.getCommenter_id());
        assertEquals("commenter_user", dto.getCommenter_username());
        assertEquals(2L, dto.getCommented_id());
        assertEquals("commented_user", dto.getCommented_username());
        assertEquals(10L, dto.getSession_id());
        assertEquals("Test Routine", dto.getRoutine_name());
        assertEquals("Great workout!", dto.getText());
        assertEquals(now, dto.getCreatedAt());
        assertFalse(dto.isRead());
    }

    @Test
    void testSetters() {
        User commenter = mock(User.class);
        when(commenter.getId()).thenReturn(1L);
        when(commenter.getNombreUsuario()).thenReturn("commenter");

        User commented = mock(User.class);
        when(commented.getId()).thenReturn(2L);
        when(commented.getNombreUsuario()).thenReturn("commented");

        Routine routine = mock(Routine.class);
        when(routine.getName()).thenReturn("Routine");

        WorkoutSession session = mock(WorkoutSession.class);
        when(session.getId()).thenReturn(10L);
        when(session.getRoutine()).thenReturn(routine);

        CommentDto dto = new CommentDto(1L, session, commenter, commented, "text", Instant.now());

        dto.setCommenter_id(100L);
        dto.setCommenter_username("new_commenter");
        dto.setCommented_id(200L);
        dto.setCommented_username("new_commented");
        dto.setSession_id(300L);
        dto.setRoutine_name("New Routine");
        dto.setText("New text");
        Instant newTime = Instant.now();
        dto.setCreatedAt(newTime);
        dto.setRead(true);

        assertEquals(100L, dto.getCommenter_id());
        assertEquals("new_commenter", dto.getCommenter_username());
        assertEquals(200L, dto.getCommented_id());
        assertEquals("new_commented", dto.getCommented_username());
        assertEquals(300L, dto.getSession_id());
        assertEquals("New Routine", dto.getRoutine_name());
        assertEquals("New text", dto.getText());
        assertEquals(newTime, dto.getCreatedAt());
        assertTrue(dto.isRead());
    }
}
