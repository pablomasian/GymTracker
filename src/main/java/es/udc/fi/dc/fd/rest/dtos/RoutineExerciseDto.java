package es.udc.fi.dc.fd.rest.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// DTO para ejercicio dentro de una rutina (sets y repeticiones)
public class RoutineExerciseDto {

    private Long id;
    private Long routineId;
    private Long exerciseId;
    private int sets;
    private int repetitions;

    public RoutineExerciseDto() {}

    public RoutineExerciseDto(Long id, Long routineId, Long exerciseId, int sets, int repetitions) {
        this.id = id;
        this.routineId = routineId;
        this.exerciseId = exerciseId;
        this.sets = sets;
        this.repetitions = repetitions;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @NotNull
    public Long getRoutineId() { return routineId; }
    public void setRoutineId(Long routineId) { this.routineId = routineId; }

    @NotNull
    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }

    @Min(1)
    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    @Min(1)
    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }
}
