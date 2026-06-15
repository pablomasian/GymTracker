package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class RoutineExerciseWithExerciseDtoTest {

    @Test
    public void testDefaultConstructor() {
        RoutineExerciseWithExerciseDto dto = new RoutineExerciseWithExerciseDto();
        assertNull(dto.getId());
        assertNull(dto.getExerciseId());
        assertNull(dto.getExerciseName());
        assertEquals(0, dto.getSets());
        assertEquals(0, dto.getRepetitions());
        assertNull(dto.getWeight());
        assertNull(dto.getTargetDistance());
        assertNull(dto.getTargetDuration());
        assertNull(dto.getExerciseType());
    }

    @Test
    public void testStrengthConstructor() {
        RoutineExerciseWithExerciseDto dto = new RoutineExerciseWithExerciseDto(
                1L, 2L, "Bench Press", 3, 10, BigDecimal.valueOf(80.5));

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getExerciseId());
        assertEquals("Bench Press", dto.getExerciseName());
        assertEquals(3, dto.getSets());
        assertEquals(10, dto.getRepetitions());
        assertEquals(BigDecimal.valueOf(80.5), dto.getWeight());
        assertEquals("STRENGTH", dto.getExerciseType());
    }

    @Test
    public void testFullConstructor() {
        RoutineExerciseWithExerciseDto dto = new RoutineExerciseWithExerciseDto(
                1L, 2L, "Running", 1, 0, null,
                BigDecimal.valueOf(5.5), 30, "CARDIO");

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getExerciseId());
        assertEquals("Running", dto.getExerciseName());
        assertEquals(1, dto.getSets());
        assertEquals(0, dto.getRepetitions());
        assertNull(dto.getWeight());
        assertEquals(BigDecimal.valueOf(5.5), dto.getTargetDistance());
        assertEquals(30, dto.getTargetDuration());
        assertEquals("CARDIO", dto.getExerciseType());
    }

    @Test
    public void testSettersAndGetters() {
        RoutineExerciseWithExerciseDto dto = new RoutineExerciseWithExerciseDto();

        dto.setId(1L);
        dto.setExerciseId(2L);
        dto.setExerciseName("Squat");
        dto.setSets(5);
        dto.setRepetitions(5);
        dto.setWeight(BigDecimal.TEN);
        dto.setTargetDistance(BigDecimal.valueOf(10.0));
        dto.setTargetDuration(45);
        dto.setExerciseType("STRENGTH");

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getExerciseId());
        assertEquals("Squat", dto.getExerciseName());
        assertEquals(5, dto.getSets());
        assertEquals(5, dto.getRepetitions());
        assertEquals(BigDecimal.TEN, dto.getWeight());
        assertEquals(BigDecimal.valueOf(10.0), dto.getTargetDistance());
        assertEquals(45, dto.getTargetDuration());
        assertEquals("STRENGTH", dto.getExerciseType());
    }
}
