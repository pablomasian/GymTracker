package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Test;

import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;

public class WorkoutSessionConversorTest {

    @Test
    public void testToAndFromWorkoutSessionDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("jdoe");

        Routine routine = new Routine("Legs");
        routine.setId(2L);

        LocalDateTime fecha = LocalDateTime.of(2021, 6, 10, 9, 0);
        WorkoutSession session = new WorkoutSession(user, routine, fecha);
        session.setId(50L);
        session.setStartTime(LocalDateTime.of(2021,6,10,9,0));
        session.setEndTime(LocalDateTime.of(2021,6,10,10,0));

        WorkoutSessionDto dto = WorkoutSessionConversor.toWorkoutSessionDto(session);

    assertEquals(Long.valueOf(session.getId()), dto.getId());
    assertEquals(Long.valueOf(user.getId()), dto.getUserId());
    assertEquals(user.getUsername(), dto.getUserName());
    assertEquals(Long.valueOf(routine.getId()), dto.getRoutineId());
    assertEquals(routine.getName(), dto.getRoutineName());
    assertEquals(fecha, dto.getFecha());
        // liked default should be false
        assertEquals(false, dto.isLiked());

        // Now convert back to entity
        WorkoutSession converted = WorkoutSessionConversor.toWorkoutSession(dto, user, routine);
    assertEquals(dto.getId(), converted.getId());
    assertEquals(dto.getFecha(), converted.getFecha());
    assertEquals(dto.getStartTime(), converted.getStartTime());
    assertEquals(dto.getEndTime(), converted.getEndTime());
    }
}
