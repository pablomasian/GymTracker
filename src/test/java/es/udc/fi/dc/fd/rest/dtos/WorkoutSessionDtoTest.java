package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class WorkoutSessionDtoTest {

    @Test
    void testDefaultConstructor() {
        WorkoutSessionDto dto = new WorkoutSessionDto();
        assertNull(dto.getId());
        assertNull(dto.getUserId());
        assertNull(dto.getUserName());
        assertNull(dto.getRoutineId());
        assertNull(dto.getRoutineName());
        assertNull(dto.getFecha());
        assertNull(dto.getStartTime());
        assertNull(dto.getEndTime());
        assertFalse(dto.isLiked());
    }

    @Test
    void testParameterizedConstructor() {
        LocalDateTime fecha = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();

        WorkoutSessionDto dto = new WorkoutSessionDto(
                1L, 2L, "user123", 3L, "Test Routine", fecha, start, end, true);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getUserId());
        assertEquals("user123", dto.getUserName());
        assertEquals(3L, dto.getRoutineId());
        assertEquals("Test Routine", dto.getRoutineName());
        assertEquals(fecha, dto.getFecha());
        assertEquals(start, dto.getStartTime());
        assertEquals(end, dto.getEndTime());
        assertTrue(dto.isLiked());
    }

    @Test
    void testSetters() {
        WorkoutSessionDto dto = new WorkoutSessionDto();
        LocalDateTime fecha = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().minusMinutes(30);
        LocalDateTime end = LocalDateTime.now();

        dto.setId(10L);
        dto.setUserId(20L);
        dto.setUserName("testuser");
        dto.setRoutineId(30L);
        dto.setRoutineName("My Routine");
        dto.setFecha(fecha);
        dto.setStartTime(start);
        dto.setEndTime(end);
        dto.setLiked(true);

        assertEquals(10L, dto.getId());
        assertEquals(20L, dto.getUserId());
        assertEquals("testuser", dto.getUserName());
        assertEquals(30L, dto.getRoutineId());
        assertEquals("My Routine", dto.getRoutineName());
        assertEquals(fecha, dto.getFecha());
        assertEquals(start, dto.getStartTime());
        assertEquals(end, dto.getEndTime());
        assertTrue(dto.isLiked());
    }
}
