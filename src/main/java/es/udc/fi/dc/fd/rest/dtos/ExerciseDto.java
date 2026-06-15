package es.udc.fi.dc.fd.rest.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO de ejercicio con validaciones básicas
public class ExerciseDto {
    private Long id;
    private String name;
    private String description;
    private String muscles;
    private String equipment;
    private String imageUrl;
    private boolean blocked;
    private String exerciseType; // "STRENGTH" o "CARDIO"

    public ExerciseDto() {
    }

    public ExerciseDto(Long id, String name, String description, String muscles, String equipment) {
        this.id = id;
        this.name = name != null ? name.trim() : null;
        this.description = description != null ? description.trim() : null;
        this.muscles = muscles != null ? muscles.trim() : null;
        this.equipment = equipment != null ? equipment.trim() : null;
        this.blocked = false;
        this.exerciseType = "STRENGTH"; // default
    }

    public ExerciseDto(Long id, String name, String description, String muscles, String equipment, boolean blocked) {
        this.id = id;
        this.name = name != null ? name.trim() : null;
        this.description = description != null ? description.trim() : null;
        this.muscles = muscles != null ? muscles.trim() : null;
        this.equipment = equipment != null ? equipment.trim() : null;
        this.blocked = blocked;
        this.exerciseType = "STRENGTH"; // default
    }

    public ExerciseDto(Long id, String name, String description, String muscles, String equipment, String imageUrl,
            boolean blocked) {
        this.id = id;
        this.name = name != null ? name.trim() : null;
        this.description = description != null ? description.trim() : null;
        this.muscles = muscles != null ? muscles.trim() : null;
        this.equipment = equipment != null ? equipment.trim() : null;
        this.imageUrl = imageUrl != null ? imageUrl.trim() : null;
        this.blocked = blocked;
        this.exerciseType = "STRENGTH"; // default
    }

    public ExerciseDto(Long id, String name, String description, String muscles, String equipment, String imageUrl,
            boolean blocked, String exerciseType) {
        this.id = id;
        this.name = name != null ? name.trim() : null;
        this.description = description != null ? description.trim() : null;
        this.muscles = muscles != null ? muscles.trim() : null;
        this.equipment = equipment != null ? equipment.trim() : null;
        this.imageUrl = imageUrl != null ? imageUrl.trim() : null;
        this.blocked = blocked;
        this.exerciseType = exerciseType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    @Size(min = 1, max = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    @Size(max = 255)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    @Size(max = 100)
    public String getMuscles() {
        return muscles;
    }

    public void setMuscles(String muscles) {
        this.muscles = muscles != null ? muscles.trim() : null;
    }

    @Size(max = 100)
    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment != null ? equipment.trim() : null;
    }

    @Size(max = 500)
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl != null ? imageUrl.trim() : null;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }
}
