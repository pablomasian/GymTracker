package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ExerciseDtoTest {

    @Test
    void testDefaultConstructor() {
        ExerciseDto dto = new ExerciseDto();
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getMuscles());
        assertNull(dto.getEquipment());
        assertNull(dto.getImageUrl());
        assertFalse(dto.isBlocked());
        assertNull(dto.getExerciseType());
    }

    @Test
    void testConstructor5Args() {
        ExerciseDto dto = new ExerciseDto(1L, "Bench Press", "Chest exercise", "Chest", "Barbell");

        assertEquals(1L, dto.getId());
        assertEquals("Bench Press", dto.getName());
        assertEquals("Chest exercise", dto.getDescription());
        assertEquals("Chest", dto.getMuscles());
        assertEquals("Barbell", dto.getEquipment());
        assertFalse(dto.isBlocked());
        assertEquals("STRENGTH", dto.getExerciseType());
    }

    @Test
    void testConstructor5ArgsWithNulls() {
        ExerciseDto dto = new ExerciseDto(1L, null, null, null, null);

        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getMuscles());
        assertNull(dto.getEquipment());
    }

    @Test
    void testConstructor6Args() {
        ExerciseDto dto = new ExerciseDto(1L, "Squat", "Leg exercise", "Legs", "Barbell", true);

        assertEquals(1L, dto.getId());
        assertEquals("Squat", dto.getName());
        assertTrue(dto.isBlocked());
        assertEquals("STRENGTH", dto.getExerciseType());
    }

    @Test
    void testConstructor7Args() {
        ExerciseDto dto = new ExerciseDto(1L, "Deadlift", "Back exercise", "Back", "Barbell", "/img/deadlift.png",
                false);

        assertEquals("Deadlift", dto.getName());
        assertEquals("/img/deadlift.png", dto.getImageUrl());
        assertFalse(dto.isBlocked());
        assertEquals("STRENGTH", dto.getExerciseType());
    }

    @Test
    void testConstructor7ArgsWithNulls() {
        ExerciseDto dto = new ExerciseDto(1L, null, null, null, null, null, false);

        assertNull(dto.getImageUrl());
    }

    @Test
    void testConstructor8Args() {
        ExerciseDto dto = new ExerciseDto(1L, "Running", "Cardio", "Legs", "None", "/img/running.png", false, "CARDIO");

        assertEquals("Running", dto.getName());
        assertEquals("CARDIO", dto.getExerciseType());
    }

    @Test
    void testSetters() {
        ExerciseDto dto = new ExerciseDto();

        dto.setId(10L);
        dto.setName("  Pull Up  ");
        dto.setDescription("  Back exercise  ");
        dto.setMuscles("  Back  ");
        dto.setEquipment("  Pull up bar  ");
        dto.setImageUrl("  /img/pullup.png  ");
        dto.setBlocked(true);
        dto.setExerciseType("STRENGTH");

        assertEquals(10L, dto.getId());
        assertEquals("Pull Up", dto.getName());
        assertEquals("Back exercise", dto.getDescription());
        assertEquals("Back", dto.getMuscles());
        assertEquals("Pull up bar", dto.getEquipment());
        assertEquals("/img/pullup.png", dto.getImageUrl());
        assertTrue(dto.isBlocked());
        assertEquals("STRENGTH", dto.getExerciseType());
    }

    @Test
    void testSettersWithNulls() {
        ExerciseDto dto = new ExerciseDto();

        dto.setName(null);
        dto.setDescription(null);
        dto.setMuscles(null);
        dto.setEquipment(null);
        dto.setImageUrl(null);

        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getMuscles());
        assertNull(dto.getEquipment());
        assertNull(dto.getImageUrl());
    }
}
