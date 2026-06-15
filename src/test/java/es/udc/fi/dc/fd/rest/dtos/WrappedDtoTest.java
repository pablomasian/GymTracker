package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.udc.fi.dc.fd.rest.dtos.WrappedDto.TopExerciseDto;

public class WrappedDtoTest {

    @Test
    void testDefaultConstructor() {
        WrappedDto dto = new WrappedDto();
        assertEquals(0, dto.getYear());
        assertNull(dto.getBestMonth());
        assertEquals(0, dto.getTotalWorkouts());
    }

    @Test
    void testAllSettersAndGetters() {
        WrappedDto dto = new WrappedDto();

        dto.setYear(2024);
        dto.setBestMonth("December");
        dto.setBestMonthWorkouts(15);
        dto.setTotalWorkouts(120);
        dto.setTotalWeightLifted(BigDecimal.valueOf(50000.5));
        dto.setWeightComparison("10 elephants");
        dto.setFriendsRanking(1);
        dto.setTotalFriends(50);
        dto.setTopMuscleGroup("Chest");
        dto.setTopMuscleGroupCount(200);
        dto.setFavoriteCoachName("Coach John");
        dto.setFavoriteCoachId(5L);
        dto.setRoutinesFromFavoriteCoach(30);
        dto.setLikesGiven(100);
        dto.setLikesReceived(150);
        dto.setCommentsGiven(25);
        dto.setCommentsReceived(40);
        dto.setTopInteractionUserName("Jane");
        dto.setTopInteractionUserId(10L);
        dto.setTopInteractionCount(75);
        dto.setCurrentStreak(7);
        dto.setLongestStreak(21);

        assertEquals(2024, dto.getYear());
        assertEquals("December", dto.getBestMonth());
        assertEquals(15, dto.getBestMonthWorkouts());
        assertEquals(120, dto.getTotalWorkouts());
        assertEquals(BigDecimal.valueOf(50000.5), dto.getTotalWeightLifted());
        assertEquals("10 elephants", dto.getWeightComparison());
        assertEquals(1, dto.getFriendsRanking());
        assertEquals(50, dto.getTotalFriends());
        assertEquals("Chest", dto.getTopMuscleGroup());
        assertEquals(200, dto.getTopMuscleGroupCount());
        assertEquals("Coach John", dto.getFavoriteCoachName());
        assertEquals(5L, dto.getFavoriteCoachId());
        assertEquals(30, dto.getRoutinesFromFavoriteCoach());
        assertEquals(100, dto.getLikesGiven());
        assertEquals(150, dto.getLikesReceived());
        assertEquals(25, dto.getCommentsGiven());
        assertEquals(40, dto.getCommentsReceived());
        assertEquals("Jane", dto.getTopInteractionUserName());
        assertEquals(10L, dto.getTopInteractionUserId());
        assertEquals(75, dto.getTopInteractionCount());
        assertEquals(7, dto.getCurrentStreak());
        assertEquals(21, dto.getLongestStreak());
    }

    @Test
    void testTopExercises() {
        WrappedDto dto = new WrappedDto();
        TopExerciseDto ex1 = new TopExerciseDto(1L, "Bench Press", "/img/bench.png", 50);
        TopExerciseDto ex2 = new TopExerciseDto(2L, "Squat", "/img/squat.png", 40);

        dto.setTopExercises(Arrays.asList(ex1, ex2));

        List<TopExerciseDto> exercises = dto.getTopExercises();
        assertEquals(2, exercises.size());
        assertEquals("Bench Press", exercises.get(0).getExerciseName());
        assertEquals("Squat", exercises.get(1).getExerciseName());
    }

    // =============== TopExerciseDto Tests ===============

    @Test
    void testTopExerciseDtoDefaultConstructor() {
        TopExerciseDto dto = new TopExerciseDto();
        assertNull(dto.getExerciseId());
        assertNull(dto.getExerciseName());
        assertNull(dto.getImageUrl());
        assertEquals(0, dto.getCount());
    }

    @Test
    void testTopExerciseDtoParameterizedConstructor() {
        TopExerciseDto dto = new TopExerciseDto(1L, "Deadlift", "/img/deadlift.png", 100);

        assertEquals(1L, dto.getExerciseId());
        assertEquals("Deadlift", dto.getExerciseName());
        assertEquals("/img/deadlift.png", dto.getImageUrl());
        assertEquals(100, dto.getCount());
    }

    @Test
    void testTopExerciseDtoSettersAndGetters() {
        TopExerciseDto dto = new TopExerciseDto();

        dto.setExerciseId(5L);
        dto.setExerciseName("Pull Up");
        dto.setImageUrl("/img/pullup.png");
        dto.setCount(75);

        assertEquals(5L, dto.getExerciseId());
        assertEquals("Pull Up", dto.getExerciseName());
        assertEquals("/img/pullup.png", dto.getImageUrl());
        assertEquals(75, dto.getCount());
    }
}
