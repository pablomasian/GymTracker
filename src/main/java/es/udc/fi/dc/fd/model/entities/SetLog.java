package es.udc.fi.dc.fd.model.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "registro_serie")
public class SetLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sesion")
    private WorkoutSession session;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ejercicio")
    private Exercise exercise;

    private int numeroSerie;
    private int repeticiones;
    private BigDecimal peso;
    // Campos para ejercicios de cardio (nullables)
    private BigDecimal distancia; // km
    private Integer duracion; // minutos

    // Requerido por JPA (constructor sin argumentos)
    public SetLog() {
    }

    // Crea un registro de serie para una sesión y ejercicio (fuerza)
    public SetLog(WorkoutSession session, Exercise exercise, int numeroSerie, int repeticiones, BigDecimal peso) {
        this.session = session;
        this.exercise = exercise;
        this.numeroSerie = numeroSerie;
        this.repeticiones = repeticiones;
        this.peso = peso;
    }

    // Constructor para ejercicios de cardio
    public SetLog(WorkoutSession session, Exercise exercise, int numeroSerie, BigDecimal distancia, Integer duracion) {
        this.session = session;
        this.exercise = exercise;
        this.numeroSerie = numeroSerie;
        this.distancia = distancia;
        this.duracion = duracion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkoutSession getSession() {
        return session;
    }

    public void setSession(WorkoutSession session) {
        this.session = session;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public int getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(int numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    @Column(name = "distancia", precision = 8, scale = 2)
    public BigDecimal getDistancia() {
        return distancia;
    }

    public void setDistancia(BigDecimal distancia) {
        this.distancia = distancia;
    }

    @Column(name = "duracion")
    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }
}
