package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

import es.udc.fi.dc.fd.model.entities.Comment;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;

public class CommentConversorTest {

    @Test
    public void testToCommentDto() {
        User commenter = new User();
        commenter.setId(10L);
        commenter.setNombreUsuario("commenterName");

        User commented = new User();
        commented.setId(11L);
        commented.setNombreUsuario("commentedName");

        User sessionUser = new User();
        sessionUser.setId(12L);
        sessionUser.setUsername("sessionUser");

        Routine routine = new Routine("MyRoutine");
        routine.setId(5L);

        WorkoutSession session = new WorkoutSession(sessionUser, routine, LocalDateTime.of(2020,1,1,10,0));
        session.setId(100L);

    LocalDateTime localDateTime = LocalDateTime.of(2020,1,1,10,5);
    Instant fecha = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    // Use available constructor and set commented explicitly
    Comment comment = new Comment(session, commenter, "Nice session", fecha);
    comment.setCommented(commented);

        CommentDto dto = CommentConversor.toCommentDto(comment);

        assertEquals(commenter.getId(), dto.getCommenter_id());
        assertEquals(commenter.getNombreUsuario(), dto.getCommenter_username());
        assertEquals(commented.getId(), dto.getCommented_id());
        assertEquals(commented.getNombreUsuario(), dto.getCommented_username());
        assertEquals(session.getId(), dto.getSession_id());
        assertEquals(routine.getName(), dto.getRoutine_name());
        assertEquals("Nice session", dto.getText());
        assertEquals(fecha, dto.getCreatedAt());
    }

}
