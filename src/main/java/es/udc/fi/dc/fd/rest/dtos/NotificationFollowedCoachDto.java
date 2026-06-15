package es.udc.fi.dc.fd.rest.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

// DTO de notificación cuando un coach seguido publica una rutina
public class NotificationFollowedCoachDto implements Serializable {

    private Long id;
    private String routineName;
    private Long userId;      // id del destinatario
    private Long coachId;     // id del coach
    private String coachName;
    private boolean isRead;
    private LocalDateTime createdAt;
     // nombre o username del coach

    // --- Constructors ---
    public NotificationFollowedCoachDto() {}

    public NotificationFollowedCoachDto(Long id, String routineName, Long userId,
                                        Long coachId, String coachName, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.routineName = routineName;
        this.userId = userId;
        this.coachId = coachId;
        this.coachName = coachName;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoutineName() {
        return routineName;
    }

    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public boolean isRead() {
        return isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }
}
