package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class NotificationFollowedCoachDtoTest {

    @Test
    void testDefaultConstructor() {
        NotificationFollowedCoachDto dto = new NotificationFollowedCoachDto();
        assertNull(dto.getId());
        assertNull(dto.getRoutineName());
        assertNull(dto.getUserId());
        assertNull(dto.getCoachId());
        assertNull(dto.getCoachName());
        assertNull(dto.getCreatedAt());
        assertFalse(dto.isRead());
    }

    @Test
    void testParameterizedConstructor() {
        LocalDateTime now = LocalDateTime.now();
        NotificationFollowedCoachDto dto = new NotificationFollowedCoachDto(
                1L, "Full Body Workout", 2L, 3L, "CoachJohn", true, now);

        assertEquals(1L, dto.getId());
        assertEquals("Full Body Workout", dto.getRoutineName());
        assertEquals(2L, dto.getUserId());
        assertEquals(3L, dto.getCoachId());
        assertEquals("CoachJohn", dto.getCoachName());
        assertTrue(dto.isRead());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    void testSetters() {
        NotificationFollowedCoachDto dto = new NotificationFollowedCoachDto();
        LocalDateTime now = LocalDateTime.now();

        dto.setId(10L);
        dto.setRoutineName("New Routine");
        dto.setUserId(20L);
        dto.setCoachId(30L);
        dto.setCoachName("NewCoach");
        dto.setRead(true);
        dto.setCreatedAt(now);

        assertEquals(10L, dto.getId());
        assertEquals("New Routine", dto.getRoutineName());
        assertEquals(20L, dto.getUserId());
        assertEquals(30L, dto.getCoachId());
        assertEquals("NewCoach", dto.getCoachName());
        assertTrue(dto.isRead());
        assertEquals(now, dto.getCreatedAt());
    }
}
