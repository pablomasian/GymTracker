package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class UserStatisticsDtoTest {

    @Test
    void testConstructorAndGetters() {
        Map<String, Long> muscles = new HashMap<>();
        muscles.put("Chest", 50L);
        muscles.put("Back", 40L);

        ExerciseStatsDto ex1 = new ExerciseStatsDto(1L, "Bench Press", 10L, 100L);
        List<ExerciseStatsDto> topExercises = Arrays.asList(ex1);

        List<Long> workoutsPerWeek = Arrays.asList(3L, 4L, 5L, 2L);

        UserStatisticsDto dto = new UserStatisticsDto(
                100L, 45.5, 500L, 5000L, BigDecimal.valueOf(50000),
                3.5, "Full Body", muscles, topExercises, workoutsPerWeek);

        assertEquals(100L, dto.getTotalWorkouts());
        assertEquals(45.5, dto.getAverageDurationMinutes());
        assertEquals(500L, dto.getTotalSets());
        assertEquals(5000L, dto.getTotalReps());
        assertEquals(BigDecimal.valueOf(50000), dto.getTotalWeightLifted());
        assertEquals(3.5, dto.getWorkoutFrequency());
        assertEquals("Full Body", dto.getMostFrequentRoutine());
        assertNotNull(dto.getMuscleDistribution());
        assertEquals(50L, dto.getMuscleDistribution().get("Chest"));
        assertNotNull(dto.getTopExercises());
        assertEquals(1, dto.getTopExercises().size());
        assertNotNull(dto.getWorkoutsPerWeek());
        assertEquals(4, dto.getWorkoutsPerWeek().size());
    }

    @Test
    void testSetters() {
        Map<String, Long> muscles = new HashMap<>();
        UserStatisticsDto dto = new UserStatisticsDto(0, 0, 0, 0, BigDecimal.ZERO, 0, "", null, null, null);

        dto.setTotalWorkouts(50);
        dto.setAverageDurationMinutes(30.0);
        dto.setTotalSets(200);
        dto.setTotalReps(2000);
        dto.setTotalWeightLifted(BigDecimal.valueOf(25000));
        dto.setWorkoutFrequency(4.0);
        dto.setMostFrequentRoutine("Upper Body");
        dto.setMuscleDistribution(muscles);
        dto.setTopExercises(null);
        dto.setWorkoutsPerWeek(null);

        assertEquals(50, dto.getTotalWorkouts());
        assertEquals(30.0, dto.getAverageDurationMinutes());
        assertEquals(200, dto.getTotalSets());
        assertEquals(2000, dto.getTotalReps());
        assertEquals(BigDecimal.valueOf(25000), dto.getTotalWeightLifted());
        assertEquals(4.0, dto.getWorkoutFrequency());
        assertEquals("Upper Body", dto.getMostFrequentRoutine());
    }
}
