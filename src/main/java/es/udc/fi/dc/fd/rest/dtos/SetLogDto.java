package es.udc.fi.dc.fd.rest.dtos;

import java.math.BigDecimal;

// DTO de registro de serie realizada en una sesión
public class SetLogDto {

    private Long id;
    private Long sessionId;
    private Long exerciseId;
    private String exerciseName; // opcional, para mostrar el nombre del ejercicio
    private String imageUrl; // URL de la imagen representativa del ejercicio
    private int numeroSerie;
    private int repeticiones;
    private BigDecimal peso;
    // Campos para cardio
    private BigDecimal distancia; // km
    private Integer duracion; // minutos
    private String exerciseType; // "STRENGTH" o "CARDIO"

    public SetLogDto() {
    }

    public SetLogDto(Long id, Long sessionId, Long exerciseId, String exerciseName, String imageUrl, int numeroSerie,
            int repeticiones, BigDecimal peso) {
        this.id = id;
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.imageUrl = imageUrl;
        this.numeroSerie = numeroSerie;
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.exerciseType = "STRENGTH";
    }

    public SetLogDto(Long id, Long sessionId, Long exerciseId, String exerciseName, String imageUrl, int numeroSerie,
            int repeticiones, BigDecimal peso, BigDecimal distancia, Integer duracion, String exerciseType) {
        this.id = id;
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.imageUrl = imageUrl;
        this.numeroSerie = numeroSerie;
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.distancia = distancia;
        this.duracion = duracion;
        this.exerciseType = exerciseType;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public BigDecimal getDistancia() {
        return distancia;
    }

    public void setDistancia(BigDecimal distancia) {
        this.distancia = distancia;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }
}
