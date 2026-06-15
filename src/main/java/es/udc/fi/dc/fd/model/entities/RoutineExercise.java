package es.udc.fi.dc.fd.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "rutina_ejercicio")
public class RoutineExercise {
    private long id;
    private Routine routine;
    private Exercise exercise;
    private int sets;
    private int repetitions;
    private java.math.BigDecimal weight; // kg
    // Campos para ejercicios de cardio
    private java.math.BigDecimal targetDistance; // km
    private Integer targetDuration; // minutos

    // Requerido por JPA (constructor sin argumentos)
    public RoutineExercise() {
    }

    // Crea el vínculo rutina-ejercicio con series y repeticiones
    public RoutineExercise(Routine routine, Exercise exercise, int sets, int repetitions) {
        this.routine = routine;
        this.exercise = exercise;
        this.sets = sets;
        this.repetitions = repetitions;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutina_ejercicio")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rutina")
    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ejercicio")
    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    @Column(name = "num_series", nullable = false)
    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    @Column(name = "num_repeticiones", nullable = false)
    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    @Column(name = "peso", precision = 6, scale = 2)
    public java.math.BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(java.math.BigDecimal weight) {
        this.weight = weight;
    }

    @Column(name = "distancia_objetivo", precision = 8, scale = 2)
    public java.math.BigDecimal getTargetDistance() {
        return targetDistance;
    }

    public void setTargetDistance(java.math.BigDecimal targetDistance) {
        this.targetDistance = targetDistance;
    }

    @Column(name = "duracion_objetivo")
    public Integer getTargetDuration() {
        return targetDuration;
    }

    public void setTargetDuration(Integer targetDuration) {
        this.targetDuration = targetDuration;
    }

    @Override
    // Formato rápido: "Ejercicio (SxR)"
    public String toString() {
        return exercise + " (" + sets + "x" + repetitions + ")";
    }
}
