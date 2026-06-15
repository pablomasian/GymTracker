package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class LogWorkoutRequestDtoTest {

    @Test
    void testSettersAndGetters() {
        LogWorkoutRequestDto dto = new LogWorkoutRequestDto();
        LocalDateTime now = LocalDateTime.now();

        LogSetDto set1 = new LogSetDto();
        set1.setExerciseId(1L);
        LogSetDto set2 = new LogSetDto();
        set2.setExerciseId(2L);
        List<LogSetDto> sets = Arrays.asList(set1, set2);

        dto.setRoutineId(10L);
        dto.setDate(now);
        dto.setDurationMinutes(60);
        dto.setSets(sets);

        assertEquals(10L, dto.getRoutineId());
        assertEquals(now, dto.getDate());
        assertEquals(60, dto.getDurationMinutes());
        assertEquals(2, dto.getSets().size());
        assertEquals(1L, dto.getSets().get(0).getExerciseId());
    }

    @Test
    void testNullValues() {
        LogWorkoutRequestDto dto = new LogWorkoutRequestDto();

        assertNull(dto.getRoutineId());
        assertNull(dto.getDate());
        assertNull(dto.getDurationMinutes());
        assertNull(dto.getSets());
    }
}
