package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.Exercise.ExerciseType;

public class ExerciseConversorTest {

    @Test
    void testToExerciseDto() {
        Exercise exercise = new Exercise("Bench Press", "Chest exercise", "Chest");
        exercise.setId(1L);
        exercise.setEquipment("Barbell");
        exercise.setImageUrl("/img/bench.png");
        exercise.setBlocked(false);
        exercise.setExerciseType(ExerciseType.STRENGTH);

        ExerciseDto dto = ExerciseConversor.toExerciseDto(exercise);

        assertEquals(1L, dto.getId());
        assertEquals("Bench Press", dto.getName());
        assertEquals("Chest exercise", dto.getDescription());
        assertEquals("Chest", dto.getMuscles());
        assertEquals("Barbell", dto.getEquipment());
        assertEquals("/img/bench.png", dto.getImageUrl());
        assertFalse(dto.isBlocked());
        assertEquals("STRENGTH", dto.getExerciseType());
    }

    @Test
    void testToExerciseDtoWithNullType() {
        Exercise exercise = new Exercise("Running", "Cardio", "Legs");
        exercise.setId(2L);
        exercise.setExerciseType(null);

        ExerciseDto dto = ExerciseConversor.toExerciseDto(exercise);

        assertEquals("STRENGTH", dto.getExerciseType()); // defaults to STRENGTH
    }

    @Test
    void testToExerciseDtoWithCardio() {
        Exercise exercise = new Exercise("Running", "Cardio exercise", "Legs");
        exercise.setId(3L);
        exercise.setExerciseType(ExerciseType.CARDIO);

        ExerciseDto dto = ExerciseConversor.toExerciseDto(exercise);

        assertEquals("CARDIO", dto.getExerciseType());
    }

    @Test
    void testToExercise() {
        ExerciseDto dto = new ExerciseDto();
        dto.setName("Squat");
        dto.setDescription("Leg exercise");
        dto.setMuscles("Legs");
        dto.setEquipment("Barbell");
        dto.setImageUrl("/img/squat.png");
        dto.setBlocked(true);
        dto.setExerciseType("STRENGTH");

        Exercise exercise = ExerciseConversor.toExercise(dto);

        assertNotNull(exercise);
        assertEquals("Squat", exercise.getName());
        assertEquals("Leg exercise", exercise.getDescription());
        assertEquals("Legs", exercise.getMuscles());
        assertEquals("Barbell", exercise.getEquipment());
        assertEquals("/img/squat.png", exercise.getImageUrl());
        assertEquals(true, exercise.isBlocked());
        assertEquals(ExerciseType.STRENGTH, exercise.getExerciseType());
    }

    @Test
    void testToExerciseWithNullType() {
        ExerciseDto dto = new ExerciseDto();
        dto.setName("Exercise");
        dto.setDescription("Description");
        dto.setMuscles("Muscles");
        dto.setExerciseType(null);

        Exercise exercise = ExerciseConversor.toExercise(dto);

        // Exercise entity constructor defaults to STRENGTH
        assertEquals(ExerciseType.STRENGTH, exercise.getExerciseType());
    }

    @Test
    void testToExerciseWithCardio() {
        ExerciseDto dto = new ExerciseDto();
        dto.setName("Cycling");
        dto.setDescription("Cardio");
        dto.setMuscles("Legs");
        dto.setExerciseType("CARDIO");

        Exercise exercise = ExerciseConversor.toExercise(dto);

        assertEquals(ExerciseType.CARDIO, exercise.getExerciseType());
    }
}
