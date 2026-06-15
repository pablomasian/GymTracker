package es.udc.fi.dc.fd.rest.dtos;

public class ExerciseStatsDto {
    private Long exerciseId;
    private String exerciseName;
    private long totalSets;
    private long totalReps;

    public ExerciseStatsDto(Long exerciseId, String exerciseName, long totalSets, long totalReps) {
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.totalSets = totalSets;
        this.totalReps = totalReps;
    }

    // Getters y Setters
    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }
    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public long getTotalSets() { return totalSets; }
    public void setTotalSets(long totalSets) { this.totalSets = totalSets; }
    public long getTotalReps() { return totalReps; }
    public void setTotalReps(long totalReps) { this.totalReps = totalReps; }
}