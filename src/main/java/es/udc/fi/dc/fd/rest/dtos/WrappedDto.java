package es.udc.fi.dc.fd.rest.dtos;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO que contiene todas las estadísticas del Wrapped anual del usuario
 */
public class WrappedDto {

    private int year;
    private String bestMonth; // Mes con más entrenamientos
    private int bestMonthWorkouts; // Número de entrenamientos en ese mes
    private int totalWorkouts; // Total entrenamientos del año
    private BigDecimal totalWeightLifted; // Kilos totales levantados
    private String weightComparison; // "Equivale a X elefantes"
    private Integer friendsRanking; // Posición entre amigos (null si no está en top 3)
    private int totalFriends; // Total de amigos para contexto
    private List<TopExerciseDto> topExercises; // Top 3 ejercicios más hechos
    private String topMuscleGroup; // Grupo muscular más entrenado
    private int topMuscleGroupCount; // Veces entrenado
    private String favoriteCoachName; // Nombre del entrenador favorito
    private Long favoriteCoachId; // ID del entrenador favorito
    private int routinesFromFavoriteCoach; // Rutinas hechas de ese coach
    private int likesGiven; // Likes dados
    private int likesReceived; // Likes recibidos
    private int commentsGiven; // Comentarios dados
    private int commentsReceived; // Comentarios recibidos
    private String topInteractionUserName; // Usuario con más interacción
    private Long topInteractionUserId; // ID del usuario con más interacción
    private int topInteractionCount; // Número de interacciones
    private int currentStreak; // Racha actual
    private int longestStreak; // Racha más larga del año

    public WrappedDto() {
    }

    // Getters y Setters
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getBestMonth() {
        return bestMonth;
    }

    public void setBestMonth(String bestMonth) {
        this.bestMonth = bestMonth;
    }

    public int getBestMonthWorkouts() {
        return bestMonthWorkouts;
    }

    public void setBestMonthWorkouts(int bestMonthWorkouts) {
        this.bestMonthWorkouts = bestMonthWorkouts;
    }

    public int getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(int totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public BigDecimal getTotalWeightLifted() {
        return totalWeightLifted;
    }

    public void setTotalWeightLifted(BigDecimal totalWeightLifted) {
        this.totalWeightLifted = totalWeightLifted;
    }

    public String getWeightComparison() {
        return weightComparison;
    }

    public void setWeightComparison(String weightComparison) {
        this.weightComparison = weightComparison;
    }

    public Integer getFriendsRanking() {
        return friendsRanking;
    }

    public void setFriendsRanking(Integer friendsRanking) {
        this.friendsRanking = friendsRanking;
    }

    public int getTotalFriends() {
        return totalFriends;
    }

    public void setTotalFriends(int totalFriends) {
        this.totalFriends = totalFriends;
    }

    public List<TopExerciseDto> getTopExercises() {
        return topExercises;
    }

    public void setTopExercises(List<TopExerciseDto> topExercises) {
        this.topExercises = topExercises;
    }

    public String getTopMuscleGroup() {
        return topMuscleGroup;
    }

    public void setTopMuscleGroup(String topMuscleGroup) {
        this.topMuscleGroup = topMuscleGroup;
    }

    public int getTopMuscleGroupCount() {
        return topMuscleGroupCount;
    }

    public void setTopMuscleGroupCount(int topMuscleGroupCount) {
        this.topMuscleGroupCount = topMuscleGroupCount;
    }

    public String getFavoriteCoachName() {
        return favoriteCoachName;
    }

    public void setFavoriteCoachName(String favoriteCoachName) {
        this.favoriteCoachName = favoriteCoachName;
    }

    public Long getFavoriteCoachId() {
        return favoriteCoachId;
    }

    public void setFavoriteCoachId(Long favoriteCoachId) {
        this.favoriteCoachId = favoriteCoachId;
    }

    public int getRoutinesFromFavoriteCoach() {
        return routinesFromFavoriteCoach;
    }

    public void setRoutinesFromFavoriteCoach(int routinesFromFavoriteCoach) {
        this.routinesFromFavoriteCoach = routinesFromFavoriteCoach;
    }

    public int getLikesGiven() {
        return likesGiven;
    }

    public void setLikesGiven(int likesGiven) {
        this.likesGiven = likesGiven;
    }

    public int getLikesReceived() {
        return likesReceived;
    }

    public void setLikesReceived(int likesReceived) {
        this.likesReceived = likesReceived;
    }

    public int getCommentsGiven() {
        return commentsGiven;
    }

    public void setCommentsGiven(int commentsGiven) {
        this.commentsGiven = commentsGiven;
    }

    public int getCommentsReceived() {
        return commentsReceived;
    }

    public void setCommentsReceived(int commentsReceived) {
        this.commentsReceived = commentsReceived;
    }

    public String getTopInteractionUserName() {
        return topInteractionUserName;
    }

    public void setTopInteractionUserName(String topInteractionUserName) {
        this.topInteractionUserName = topInteractionUserName;
    }

    public Long getTopInteractionUserId() {
        return topInteractionUserId;
    }

    public void setTopInteractionUserId(Long topInteractionUserId) {
        this.topInteractionUserId = topInteractionUserId;
    }

    public int getTopInteractionCount() {
        return topInteractionCount;
    }

    public void setTopInteractionCount(int topInteractionCount) {
        this.topInteractionCount = topInteractionCount;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    /**
     * DTO interno para representar un ejercicio en el top
     */
    public static class TopExerciseDto {
        private Long exerciseId;
        private String exerciseName;
        private String imageUrl;
        private int count;

        public TopExerciseDto() {
        }

        public TopExerciseDto(Long exerciseId, String exerciseName, String imageUrl, int count) {
            this.exerciseId = exerciseId;
            this.exerciseName = exerciseName;
            this.imageUrl = imageUrl;
            this.count = count;
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

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
