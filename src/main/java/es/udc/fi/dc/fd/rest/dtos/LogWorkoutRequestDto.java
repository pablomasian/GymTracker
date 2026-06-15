package es.udc.fi.dc.fd.rest.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

public class LogWorkoutRequestDto {
    @NotNull
    private Long routineId;
    @NotNull
    private LocalDateTime date;
    
    private Integer durationMinutes; 

    @NotEmpty
    @Valid
    private List<LogSetDto> sets;

    // Getters y Setters
    public Long getRoutineId() { return routineId; }
    public void setRoutineId(Long routineId) { this.routineId = routineId; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public List<LogSetDto> getSets() { return sets; }
    public void setSets(List<LogSetDto> sets) { this.sets = sets; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
}