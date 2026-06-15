package es.udc.fi.dc.fd.model.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "rutinas")
public class Routine {

    public enum RoutineEstado {
        PENDING,
        APPROVED
    }
    
    private long id;
    private String name;
    private User user;
    private boolean visible = true;
    private RoutineEstado estado;
    private boolean blocked = false; 

    // Requerido por JPA (constructor sin argumentos)
    public Routine() {}

    // Crea una rutina con nombre y propietario; por defecto visible
    public Routine(String name, User user) {
        this.name = name;
        this.user = user;
        this.visible = true;
        this.estado = RoutineEstado.PENDING;
        this.blocked = false; 

    }

    // Crea una rutina con nombre; útil para tests/seeding
    public Routine (String name){
        this.name = name;
        this.visible = true;
        this.estado = RoutineEstado.PENDING;
        this.blocked = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutina")
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @Column(name = "nombre_rutina", length = 60, nullable = false)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Column(name = "visible", nullable = false)
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    public RoutineEstado getEstado() { return estado; }
    public void setEstado(RoutineEstado estado) { this.estado = estado; }

    @Column(name = "bloqueado", nullable = false)
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
}
