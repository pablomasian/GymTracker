package es.udc.fi.dc.fd.rest.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO de rutina; puede incluir info del coach y número de ejercicios
public class RoutineDto {
    private Long id;
    private String name;
    private Long coachId;
    private String coachNombreUsuario;
    private Integer exerciseCount;
    private Boolean visible;

    public RoutineDto() {}

    public RoutineDto(Long id, String name, Long coachId) {
        this.id = id;
        this.name = name != null ? name.trim() : null;
        this.coachId = coachId;
    }

    public RoutineDto(Long id, String name, Long coachId, String coachNombreUsuario, Integer exerciseCount) {
        this(id, name, coachId);
        this.coachNombreUsuario = coachNombreUsuario;
        this.exerciseCount = exerciseCount;
    }
    public RoutineDto(Long id, String name, Long coachId, String coachNombreUsuario, Integer exerciseCount, Boolean visible) {
        this(id, name, coachId, coachNombreUsuario, exerciseCount);
        this.visible = visible;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @NotNull
    @Size(min=2, max=100)
    public String getName() { return name; }
    public void setName(String name) { this.name = name != null ? name.trim() : null; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public String getCoachNombreUsuario() { return coachNombreUsuario; }
    public void setCoachNombreUsuario(String coachNombreUsuario) { this.coachNombreUsuario = coachNombreUsuario; }

    public Integer getExerciseCount() { return exerciseCount; }
    public void setExerciseCount(Integer exerciseCount) { this.exerciseCount = exerciseCount; }

    public Boolean getVisible() { return visible; }
    public void setVisible(Boolean visible) { this.visible = visible; }
}
