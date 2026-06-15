package es.udc.fi.dc.fd.model.entities;

import java.time.LocalDate;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "likes")
public class Like {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "liker_id", nullable = false)
    private User liker;

    @ManyToOne
    @JoinColumn(name = "liked_id", nullable = false)
    private User liked;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private WorkoutSession session;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant createdAt;

    @Column(name = "leido", nullable = false)
    private boolean isRead;

    public Like(User liker, User liked,  WorkoutSession session) {
        this.liker = liker;
        this.liked = liked;
        this.session = session;
        this.createdAt = Instant.now();
        this.isRead = false;
    }

    public Like(User user, WorkoutSession session, Instant createdAt) {
        this.liker = user;
        this.session = session;
        this.createdAt = createdAt;
        this.isRead = false;
    }

    public Like() {
    }

    

    public User getLiker() {
        return liker;
    }

    public WorkoutSession getSession() {
        return session;
    }

    public void setLiker(User liker) {
        this.liker = liker;
    }

    public void setSession(WorkoutSession session) {
        this.session = session;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public User getLiked() {
        return liked;
    }

    public void setLiked(User liked) {
        this.liked = liked;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Long getId() {
        return id;
    }

    

    

    


}
