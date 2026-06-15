package es.udc.fi.dc.fd.rest.dtos;

import java.time.LocalDateTime;

public class WorkoutSessionDto {

    private Long id;
    private Long userId;
    private String userName;
    private Long routineId;
    private String routineName;
    private LocalDateTime fecha;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean liked;

    public WorkoutSessionDto() {
    }

    public WorkoutSessionDto(Long id, Long userId, String userName, Long routineId, String routineName, LocalDateTime fecha, LocalDateTime startTime, LocalDateTime endTime, Boolean liked) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.routineId = routineId;
        this.routineName = routineName;
        this.fecha = fecha;
        this.startTime = startTime;
        this.endTime = endTime;
        this.liked = liked;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getRoutineId() { return routineId; }
    public void setRoutineId(Long routineId) { this.routineId = routineId; }

    public String getRoutineName() { return routineName; }
    public void setRoutineName(String routineName) { this.routineName = routineName; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) {this.liked = liked;}
    

    
}
