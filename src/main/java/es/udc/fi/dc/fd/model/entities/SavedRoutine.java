package es.udc.fi.dc.fd.model.entities;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "rutinas_guardadas", uniqueConstraints = @UniqueConstraint(columnNames = {"id_usuario","id_rutina"}))
public class SavedRoutine {
    private Long id;
    private User user;
    private Routine routine;
    private Instant createdAt = Instant.now();

    // Requerido por JPA (constructor sin argumentos)
    public SavedRoutine() {}
    // Marca una rutina como guardada por un usuario
    public SavedRoutine(User user, Routine routine) { this.user = user; this.routine = routine; }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutina_guardada")
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rutina")
    public Routine getRoutine() { return routine; }
    public void setRoutine(Routine routine) { this.routine = routine; }

    @Column(name = "fecha_creacion", nullable=false)
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
