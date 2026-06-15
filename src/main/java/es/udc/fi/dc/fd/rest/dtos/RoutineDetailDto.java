package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;

// DTO de detalle de rutina con sus ejercicios
public class RoutineDetailDto {
    private Long id;
    private String name;
    private Long userId;
    private List<RoutineExerciseWithExerciseDto> exercises;

    public RoutineDetailDto() {}

    public RoutineDetailDto(Long id, String name, Long userId, List<RoutineExerciseWithExerciseDto> exercises) {
        this.id = id; this.name = name; this.userId = userId; this.exercises = exercises;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<RoutineExerciseWithExerciseDto> getExercises() { return exercises; }
    public void setExercises(List<RoutineExerciseWithExerciseDto> exercises) { this.exercises = exercises; }
}
