package es.udc.fi.dc.fd.rest.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Petición para añadir un ejercicio a una rutina (sets, reps, peso opcional)
public class CreateRoutineExerciseRequest {
    @NotNull
    private Long exerciseId;

    @Min(1)
    private int sets;

    @Min(0) // 0 para cardio (no tienen reps)
    private int repetitions;

    // Peso opcional (kg). Null o 0 => peso corporal/no especificado
    private java.math.BigDecimal weight;

    // Campos para cardio
    private java.math.BigDecimal targetDistance; // km
    private Integer targetDuration; // minutos

    public CreateRoutineExerciseRequest() {
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public java.math.BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(java.math.BigDecimal weight) {
        this.weight = weight;
    }

    public java.math.BigDecimal getTargetDistance() {
        return targetDistance;
    }

    public void setTargetDistance(java.math.BigDecimal targetDistance) {
        this.targetDistance = targetDistance;
    }

    public Integer getTargetDuration() {
        return targetDuration;
    }

    public void setTargetDuration(Integer targetDuration) {
        this.targetDuration = targetDuration;
    }
}
