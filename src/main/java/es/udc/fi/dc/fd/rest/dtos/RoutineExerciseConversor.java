package es.udc.fi.dc.fd.rest.dtos;

import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineExercise;

// Conversor entre RoutineExercise y su DTO
public class RoutineExerciseConversor {

    private RoutineExerciseConversor() {}

    public static final RoutineExerciseDto toRoutineExerciseDto(RoutineExercise re) {
        return new RoutineExerciseDto(
            re.getId(),
            re.getRoutine().getId(),
            re.getExercise().getId(),
            re.getSets(),
            re.getRepetitions()
        );
    }

    public static final RoutineExercise toRoutineExercise(RoutineExerciseDto dto, Routine routine, Exercise exercise) {
        RoutineExercise re = new RoutineExercise(routine, exercise, dto.getSets(), dto.getRepetitions());
        re.setId(dto.getId());
        return re;
    }
}

