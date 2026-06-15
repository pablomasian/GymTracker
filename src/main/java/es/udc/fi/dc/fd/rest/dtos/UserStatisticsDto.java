package es.udc.fi.dc.fd.rest.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class UserStatisticsDto {

    private long totalWorkouts;
    private double averageDurationMinutes;
    private long totalSets;
    private long totalReps;
    private BigDecimal totalWeightLifted;
    private double workoutFrequency;
    private String mostFrequentRoutine;
    private Map<String, Long> muscleDistribution;
    private List<ExerciseStatsDto> topExercises;
    private List<Long> workoutsPerWeek;

    public UserStatisticsDto(long totalWorkouts, double averageDurationMinutes, long totalSets, long totalReps,
                             BigDecimal totalWeightLifted, double workoutFrequency, String mostFrequentRoutine,
                             Map<String, Long> muscleDistribution, List<ExerciseStatsDto> topExercises,
                             List<Long> workoutsPerWeek) {
        this.totalWorkouts = totalWorkouts;
        this.averageDurationMinutes = averageDurationMinutes;
        this.totalSets = totalSets;
        this.totalReps = totalReps;
        this.totalWeightLifted = totalWeightLifted;
        this.workoutFrequency = workoutFrequency;
        this.mostFrequentRoutine = mostFrequentRoutine;
        this.muscleDistribution = muscleDistribution;
        this.topExercises = topExercises;
        this.workoutsPerWeek = workoutsPerWeek;
    }

    // Getters y Setters
    public long getTotalWorkouts() { return totalWorkouts; }
    public void setTotalWorkouts(long totalWorkouts) { this.totalWorkouts = totalWorkouts; }
    public double getAverageDurationMinutes() { return averageDurationMinutes; }
    public void setAverageDurationMinutes(double averageDurationMinutes) { this.averageDurationMinutes = averageDurationMinutes; }
    public long getTotalSets() { return totalSets; }
    public void setTotalSets(long totalSets) { this.totalSets = totalSets; }
    public long getTotalReps() { return totalReps; }
    public void setTotalReps(long totalReps) { this.totalReps = totalReps; }
    public BigDecimal getTotalWeightLifted() { return totalWeightLifted; }
    public void setTotalWeightLifted(BigDecimal totalWeightLifted) { this.totalWeightLifted = totalWeightLifted; }
    public double getWorkoutFrequency() { return workoutFrequency; }
    public void setWorkoutFrequency(double workoutFrequency) { this.workoutFrequency = workoutFrequency; }
    public String getMostFrequentRoutine() { return mostFrequentRoutine; }
    public void setMostFrequentRoutine(String mostFrequentRoutine) { this.mostFrequentRoutine = mostFrequentRoutine; }
    public Map<String, Long> getMuscleDistribution() { return muscleDistribution; }
    public void setMuscleDistribution(Map<String, Long> muscleDistribution) { this.muscleDistribution = muscleDistribution; }
    public List<ExerciseStatsDto> getTopExercises() { return topExercises; }
    public void setTopExercises(List<ExerciseStatsDto> topExercises) { this.topExercises = topExercises; }
    public List<Long> getWorkoutsPerWeek() { return workoutsPerWeek; }
    public void setWorkoutsPerWeek(List<Long> workoutsPerWeek) { this.workoutsPerWeek = workoutsPerWeek; }
}