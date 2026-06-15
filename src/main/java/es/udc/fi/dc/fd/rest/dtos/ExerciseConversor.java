package es.udc.fi.dc.fd.rest.dtos;

import es.udc.fi.dc.fd.model.entities.Exercise;

// Conversor entre Exercise y ExerciseDto
public class ExerciseConversor {

    private ExerciseConversor() {
    }

    public static final ExerciseDto toExerciseDto(Exercise exercise) {
        return new ExerciseDto(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getMuscles(),
                exercise.getEquipment(),
                exercise.getImageUrl(),
                exercise.isBlocked(),
                exercise.getExerciseType() != null ? exercise.getExerciseType().name() : "STRENGTH");
    }

    public static final Exercise toExercise(ExerciseDto dto) {
        Exercise exercise = new Exercise(
                dto.getName(),
                dto.getDescription(),
                dto.getMuscles());
        exercise.setEquipment(dto.getEquipment());
        exercise.setImageUrl(dto.getImageUrl());
        exercise.setBlocked(dto.isBlocked());
        if (dto.getExerciseType() != null) {
            exercise.setExerciseType(Exercise.ExerciseType.valueOf(dto.getExerciseType()));
        }

        return exercise;
    }
}