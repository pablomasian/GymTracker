package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;
import java.util.stream.Collectors;

import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineExercise;

// Conversor para detalle de rutina (rutina + ejercicios)
public class RoutineDetailConversor {
    private RoutineDetailConversor() {
    }

    public static RoutineDetailDto toRoutineDetailDto(Routine routine, List<RoutineExercise> routineExercises) {
        List<RoutineExerciseWithExerciseDto> exerciseDtos = routineExercises.stream()
                .map(re -> new RoutineExerciseWithExerciseDto(
                        re.getId(),
                        re.getExercise().getId(),
                        re.getExercise().getName(),
                        re.getSets(),
                        re.getRepetitions(),
                        re.getWeight(),
                        re.getTargetDistance(),
                        re.getTargetDuration(),
                        re.getExercise().getExerciseType() != null ? re.getExercise().getExerciseType().name()
                                : "STRENGTH"))
                .collect(Collectors.toList());

        return new RoutineDetailDto(routine.getId(), routine.getName(), routine.getUser().getId(), exerciseDtos);
    }
}
