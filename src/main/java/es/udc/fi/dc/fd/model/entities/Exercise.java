package es.udc.fi.dc.fd.model.entities;

import jakarta.persistence.*;

// Entidad de ejercicio (tabla 'ejercicio')
@Entity
@Table(name = "ejercicio")
public class Exercise {

    // Estado de publicación/revisión del ejercicio
    public enum ExerciseEstado {
        PENDING,
        APPROVED
    }

    // Tipo de ejercicio: fuerza o cardio
    public enum ExerciseType {
        STRENGTH,
        CARDIO
    }

    // Campos mapeados a columnas en español
    private long id;
    private String name;
    private String description;
    private String muscles;
    private String equipment;
    private String imageUrl; // columna 'imagen_url'
    private ExerciseEstado estado; // columna 'estado'
    private boolean blocked; // indica si el ejercicio está bloqueado (no visible para usuarios)
    private ExerciseType exerciseType; // columna 'tipo_ejercicio'

    public Exercise() {
        this.estado = ExerciseEstado.PENDING;
        this.blocked = false;
        this.exerciseType = ExerciseType.STRENGTH;
    }

    public Exercise(String name, String description, String muscles) {
        this.name = name;
        this.description = description;
        this.muscles = muscles;
        this.estado = ExerciseEstado.PENDING;
        this.blocked = false;
        this.exerciseType = ExerciseType.STRENGTH; // default: fuerza
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejercicio")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "nombre_ejercicio", length = 60, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "descripcion")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "musculos", length = 60)
    public String getMuscles() {
        return muscles;
    }

    public void setMuscles(String muscles) {
        this.muscles = muscles;
    }

    @Column(name = "equipamiento", length = 100)
    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    @Column(name = "imagen_url", length = 500)
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    public ExerciseEstado getestado() {
        return estado;
    }

    public void setestado(ExerciseEstado estado) {
        this.estado = estado;
    }

    @Column(name = "bloqueado", nullable = false)
    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ejercicio", nullable = false, length = 20)
    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
    }
}