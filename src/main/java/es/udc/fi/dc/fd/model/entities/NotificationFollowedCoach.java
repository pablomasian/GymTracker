package es.udc.fi.dc.fd.model.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion_seguidor_coach")
public class NotificationFollowedCoach implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion_followed")
    private Long id;

    @Column(name = "nombre_rutina", nullable = false, length = 60)
    private String routineName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_coach", nullable = false)
    private User coach;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "leido", nullable = false)
    private boolean isRead;

    
// Notifica a un seguidor cuando un coach publica una rutina
    public NotificationFollowedCoach(String routineName, User user, User coach) {
        this.routineName = routineName;
        this.user = user;
        this.coach = coach;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public NotificationFollowedCoach() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getCoach() {
        return coach;
    }

    public void setCoach(User coach) {
        this.coach = coach;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

}
