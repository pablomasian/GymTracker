package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RoutineDtoTest {

    @Test
    void testDefaultConstructor() {
        RoutineDto dto = new RoutineDto();
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getCoachId());
        assertNull(dto.getCoachNombreUsuario());
        assertNull(dto.getExerciseCount());
        assertNull(dto.getVisible());
    }

    @Test
    void testConstructor3Args() {
        RoutineDto dto = new RoutineDto(1L, "My Routine", 2L);

        assertEquals(1L, dto.getId());
        assertEquals("My Routine", dto.getName());
        assertEquals(2L, dto.getCoachId());
    }

    @Test
    void testConstructor3ArgsWithNull() {
        RoutineDto dto = new RoutineDto(1L, null, 2L);

        assertNull(dto.getName());
    }

    @Test
    void testConstructor5Args() {
        RoutineDto dto = new RoutineDto(1L, "Workout Plan", 2L, "coach_user", 5);

        assertEquals(1L, dto.getId());
        assertEquals("Workout Plan", dto.getName());
        assertEquals(2L, dto.getCoachId());
        assertEquals("coach_user", dto.getCoachNombreUsuario());
        assertEquals(5, dto.getExerciseCount());
    }

    @Test
    void testConstructor6Args() {
        RoutineDto dto = new RoutineDto(1L, "Full Body", 2L, "trainer", 10, true);

        assertEquals(1L, dto.getId());
        assertEquals("Full Body", dto.getName());
        assertEquals(2L, dto.getCoachId());
        assertEquals("trainer", dto.getCoachNombreUsuario());
        assertEquals(10, dto.getExerciseCount());
        assertTrue(dto.getVisible());
    }

    @Test
    void testSetters() {
        RoutineDto dto = new RoutineDto();

        dto.setId(10L);
        dto.setName("  Upper Body  ");
        dto.setCoachId(20L);
        dto.setCoachNombreUsuario("my_coach");
        dto.setExerciseCount(8);
        dto.setVisible(true);

        assertEquals(10L, dto.getId());
        assertEquals("Upper Body", dto.getName());
        assertEquals(20L, dto.getCoachId());
        assertEquals("my_coach", dto.getCoachNombreUsuario());
        assertEquals(8, dto.getExerciseCount());
        assertTrue(dto.getVisible());
    }

    @Test
    void testSetNameNull() {
        RoutineDto dto = new RoutineDto();
        dto.setName(null);
        assertNull(dto.getName());
    }
}
