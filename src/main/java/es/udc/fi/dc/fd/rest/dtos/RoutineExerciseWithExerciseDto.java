package es.udc.fi.dc.fd.rest.dtos;

// DTO de ejercicio dentro de rutina con nombre del ejercicio y peso/cardio
public class RoutineExerciseWithExerciseDto {
    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private int sets;
    private int repetitions;
    private java.math.BigDecimal weight;
    // Campos para cardio
    private java.math.BigDecimal targetDistance; // km
    private Integer targetDuration; // minutos
    private String exerciseType; // "STRENGTH" o "CARDIO"

    public RoutineExerciseWithExerciseDto() {
    }

    public RoutineExerciseWithExerciseDto(Long id, Long exerciseId, String exerciseName, int sets, int repetitions,
            java.math.BigDecimal weight) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.repetitions = repetitions;
        this.weight = weight;
        this.exerciseType = "STRENGTH";
    }

    public RoutineExerciseWithExerciseDto(Long id, Long exerciseId, String exerciseName, int sets, int repetitions,
            java.math.BigDecimal weight, java.math.BigDecimal targetDistance,
            Integer targetDuration, String exerciseType) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.repetitions = repetitions;
        this.weight = weight;
        this.targetDistance = targetDistance;
        this.targetDuration = targetDuration;
        this.exerciseType = exerciseType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
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

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }
}
