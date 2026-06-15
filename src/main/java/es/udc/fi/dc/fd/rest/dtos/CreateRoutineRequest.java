package es.udc.fi.dc.fd.rest.dtos;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Petición para crear/actualizar una rutina con nombre y ejercicios
public class CreateRoutineRequest {
    @NotNull
    @Size(min = 2, max = 100)
    private String name;

    @Valid
    private List<CreateRoutineExerciseRequest> exercises; // opcional

    public CreateRoutineRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name != null ? name.trim() : null; }

    public List<CreateRoutineExerciseRequest> getExercises() { return exercises; }
    public void setExercises(List<CreateRoutineExerciseRequest> exercises) { this.exercises = exercises; }
}
