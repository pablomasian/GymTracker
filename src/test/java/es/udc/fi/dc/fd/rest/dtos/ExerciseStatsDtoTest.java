package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ExerciseStatsDtoTest {

    @Test
    void testConstructorAndGetters() {
        Long id = 1L;
        String name = "Push Up";
        long sets = 10;
        long reps = 150;

        ExerciseStatsDto dto = new ExerciseStatsDto(id, name, sets, reps);

        assertEquals(id, dto.getExerciseId());
        assertEquals(name, dto.getExerciseName());
        assertEquals(sets, dto.getTotalSets());
        assertEquals(reps, dto.getTotalReps());
    }

    @Test
    void testSetters() {
        ExerciseStatsDto dto = new ExerciseStatsDto(null, null, 0, 0);

        dto.setExerciseId(2L);
        dto.setExerciseName("Pull Up");
        dto.setTotalSets(5);
        dto.setTotalReps(50);

        assertEquals(2L, dto.getExerciseId());
        assertEquals("Pull Up", dto.getExerciseName());
        assertEquals(5, dto.getTotalSets());
        assertEquals(50, dto.getTotalReps());
    }
}
