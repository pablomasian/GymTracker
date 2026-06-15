package es.udc.fi.dc.fd.model.entities;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "notificacion")
public class Notification {
    private Long id;
    private User recipient;
    private String type;
    private String message;
    private boolean read;
    private Instant createdAt = Instant.now();

    // Requerido por JPA (constructor sin argumentos)
    public Notification() {}
    public Notification(User recipient, String type, String message) {
        this.recipient = recipient; this.type = type; this.message = message;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_notificacion")
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="id_destinatario")
    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    @Column(name="tipo", length=40, nullable=false)
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Column(name="mensaje", length=300)
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Column(name="leido")
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    @Column(name="fecha_creacion", nullable=false)
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
