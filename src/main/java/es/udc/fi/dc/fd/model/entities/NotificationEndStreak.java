package es.udc.fi.dc.fd.model.entities;

import java.time.Instant;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "notificacion_fin_racha")
public class NotificationEndStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion_racha")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private User usuario;

    @Column(name = "dias_racha", nullable = false)
    private int diasRacha;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDateTime fechaLimite;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "leido")
    private boolean leido = false;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUser() {
        return usuario;
    }
    public void setUser(User usuario) {
        this.usuario = usuario;
    }   
    public int getDiasRacha() {
        return diasRacha;
    }   
    public void setDiasRacha(int diasRacha) {
        this.diasRacha = diasRacha;
    }
    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }
    public void setFechaLimite(LocalDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    public boolean isLeido() {
        return leido;
    }
    public void setLeido(boolean leido) {
        this.leido = leido;
    }

}
