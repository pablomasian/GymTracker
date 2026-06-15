package es.udc.fi.dc.fd.model.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "comments")
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private WorkoutSession session;

    @ManyToOne
    private User commenter;

    @ManyToOne
    private User commented;

    private String text;


    @Column(name = "fecha_creacion", nullable = false)
    private Instant createdAt;

    @Column(name = "leido", nullable = false)
    private boolean isRead;

    public Comment(WorkoutSession session, User commenter, User commented, String text) {
        this.session = session;
        this.commenter = commenter;
        this.commented = commented;
        this.text = text;
        this.createdAt = Instant.now();
        this.isRead = false;
    }

    public Comment(WorkoutSession session, User commenter, String text, Instant createdAt) {
        this.session = session;
        this.commenter = commenter;
        this.text = text;
        this.createdAt = createdAt;
        this.isRead = false;
    }

    public Comment() {
    }


    public WorkoutSession getSession() {
        return session;
    }

    public User getCommenter() {
        return commenter;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setSession(WorkoutSession session) {
        this.session = session;
    }

    public void setCommenter(User commenter) {
        this.commenter = commenter;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getCommented() {
        return commented;
    }

    public void setCommented(User commented) {
        this.commented = commented;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    
    

}
