package es.udc.fi.dc.fd.model.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.entities.*;
import es.udc.fi.dc.fd.rest.dtos.WrappedDto;
import es.udc.fi.dc.fd.rest.dtos.WrappedDto.TopExerciseDto;

@Service
@Transactional(readOnly = true)
public class WrappedServiceImpl implements WrappedService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private WorkoutSessionDao workoutSessionDao;

    @Autowired
    private SetLogDao setLogDao;

    @Autowired
    private LikeDao likeDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private FollowDao followDao;

    @Override
    public WrappedDto getWrapped(Long userId, int year) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fechas del año
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        WrappedDto dto = new WrappedDto();
        dto.setYear(year);

        // Obtener sesiones del año
        List<WorkoutSession> yearSessions = workoutSessionDao
                .findByUserIdAndFechaAfterOrderByFechaDesc(userId, startOfYear)
                .stream()
                .filter(s -> s.getFecha().isBefore(endOfYear))
                .collect(Collectors.toList());

        dto.setTotalWorkouts(yearSessions.size());

        // Calcular mejor mes
        calculateBestMonth(yearSessions, dto);

        // Calcular peso total levantado
        calculateTotalWeight(yearSessions, dto);

        // Calcular ranking entre amigos
        calculateFriendsRanking(userId, year, dto);

        // Calcular top ejercicios
        calculateTopExercises(yearSessions, dto);

        // Calcular grupo muscular más entrenado
        calculateTopMuscleGroup(yearSessions, dto);

        // Calcular entrenador favorito
        calculateFavoriteCoach(yearSessions, dto);

        // Calcular likes y comentarios
        calculateSocialStats(userId, year, dto);

        // Calcular usuario con más interacción
        calculateTopInteraction(userId, year, dto);

        // Racha actual
        Integer streakCount = user.getStreakCount();
        dto.setCurrentStreak(streakCount != null ? streakCount : 0);
        dto.setLongestStreak(streakCount != null ? streakCount : 0);

        return dto;
    }

    private void calculateBestMonth(List<WorkoutSession> sessions, WrappedDto dto) {
        if (sessions.isEmpty()) {
            dto.setBestMonth("N/A");
            dto.setBestMonthWorkouts(0);
            return;
        }

        Map<Month, Long> workoutsByMonth = sessions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getFecha().getMonth(),
                        Collectors.counting()));

        Map.Entry<Month, Long> best = workoutsByMonth.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (best != null) {
            dto.setBestMonth(best.getKey().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            dto.setBestMonthWorkouts(best.getValue().intValue());
        }
    }

    private void calculateTotalWeight(List<WorkoutSession> sessions, WrappedDto dto) {
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (WorkoutSession session : sessions) {
            List<SetLog> setLogs = setLogDao.findBySessionId(session.getId());
            for (SetLog log : setLogs) {
                if (log.getPeso() != null && log.getRepeticiones() > 0) {
                    // Peso x repeticiones = volumen de ese set
                    BigDecimal setVolume = log.getPeso()
                            .multiply(BigDecimal.valueOf(log.getRepeticiones()));
                    totalWeight = totalWeight.add(setVolume);
                }
            }
        }

        dto.setTotalWeightLifted(totalWeight);

        // Comparación divertida
        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            // Un elefante pesa ~5000 kg
            double elephants = totalWeight.doubleValue() / 5000.0;
            if (elephants >= 1) {
                dto.setWeightComparison(String.format("¡Eso son %.1f elefantes! 🐘", elephants));
            } else {
                // Un coche pesa ~1500 kg
                double cars = totalWeight.doubleValue() / 1500.0;
                if (cars >= 1) {
                    dto.setWeightComparison(String.format("¡Eso son %.1f coches! 🚗", cars));
                } else {
                    dto.setWeightComparison(String.format("%.0f kg levantados 💪", totalWeight.doubleValue()));
                }
            }
        } else {
            dto.setWeightComparison("Start lifting weights! 💪");
        }
    }

    private void calculateFriendsRanking(Long userId, int year, WrappedDto dto) {
        // Obtener usuarios que sigo (mis "amigos")
        List<User> following = followDao.findCoachesByFollowerId(userId);

        if (following.isEmpty()) {
            dto.setFriendsRanking(null);
            dto.setTotalFriends(0);
            return;
        }

        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);

        // Contar entrenamientos de cada amigo
        Map<Long, Integer> workoutCounts = new HashMap<>();

        // Añadir el usuario actual
        int myCount = workoutSessionDao.findByUserIdAndFechaAfterOrderByFechaDesc(userId, startOfYear).size();
        workoutCounts.put(userId, myCount);

        for (User friend : following) {
            int count = workoutSessionDao.findByUserIdAndFechaAfterOrderByFechaDesc(friend.getId(), startOfYear).size();
            workoutCounts.put(friend.getId(), count);
        }

        // Ordenar por cantidad de entrenamientos
        List<Map.Entry<Long, Integer>> sorted = workoutCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        // Encontrar posición del usuario
        int ranking = 1;
        for (Map.Entry<Long, Integer> entry : sorted) {
            if (entry.getKey().equals(userId)) {
                break;
            }
            ranking++;
        }

        // Solo mostrar si está en top 3
        if (ranking <= 3) {
            dto.setFriendsRanking(ranking);
        } else {
            dto.setFriendsRanking(null);
        }
        dto.setTotalFriends(following.size());
    }

    private void calculateTopExercises(List<WorkoutSession> sessions, WrappedDto dto) {
        Map<Exercise, Integer> exerciseCounts = new HashMap<>();

        for (WorkoutSession session : sessions) {
            List<SetLog> setLogs = setLogDao.findBySessionId(session.getId());
            for (SetLog log : setLogs) {
                Exercise ex = log.getExercise();
                exerciseCounts.put(ex, exerciseCounts.getOrDefault(ex, 0) + 1);
            }
        }

        List<TopExerciseDto> topExercises = exerciseCounts.entrySet().stream()
                .sorted(Map.Entry.<Exercise, Integer>comparingByValue().reversed())
                .limit(3)
                .map(e -> new TopExerciseDto(
                        e.getKey().getId(),
                        e.getKey().getName(),
                        e.getKey().getImageUrl(),
                        e.getValue()))
                .collect(Collectors.toList());

        dto.setTopExercises(topExercises);
    }

    private void calculateTopMuscleGroup(List<WorkoutSession> sessions, WrappedDto dto) {
        Map<String, Integer> muscleCounts = new HashMap<>();

        for (WorkoutSession session : sessions) {
            List<SetLog> setLogs = setLogDao.findBySessionId(session.getId());
            for (SetLog log : setLogs) {
                String muscles = log.getExercise().getMuscles();
                if (muscles != null && !muscles.isEmpty()) {
                    // Dividir por comas y contar cada músculo
                    String[] muscleArray = muscles.split(",");
                    for (String muscle : muscleArray) {
                        String trimmed = muscle.trim();
                        if (!trimmed.isEmpty()) {
                            muscleCounts.put(trimmed, muscleCounts.getOrDefault(trimmed, 0) + 1);
                        }
                    }
                }
            }
        }

        Map.Entry<String, Integer> top = muscleCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (top != null) {
            dto.setTopMuscleGroup(top.getKey());
            dto.setTopMuscleGroupCount(top.getValue());
        } else {
            dto.setTopMuscleGroup("N/A");
            dto.setTopMuscleGroupCount(0);
        }
    }

    private void calculateFavoriteCoach(List<WorkoutSession> sessions, WrappedDto dto) {
        Map<User, Integer> coachCounts = new HashMap<>();

        for (WorkoutSession session : sessions) {
            if (session.getRoutine() != null && session.getRoutine().getUser() != null) {
                User coach = session.getRoutine().getUser();
                coachCounts.put(coach, coachCounts.getOrDefault(coach, 0) + 1);
            }
        }

        Map.Entry<User, Integer> favorite = coachCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (favorite != null) {
            dto.setFavoriteCoachName(favorite.getKey().getNombreUsuario());
            dto.setFavoriteCoachId(favorite.getKey().getId());
            dto.setRoutinesFromFavoriteCoach(favorite.getValue());
        }
    }

    private void calculateSocialStats(Long userId, int year, WrappedDto dto) {
        // Likes dados
        List<Like> likesGiven = likeDao.findAllByLikerId(userId);
        int likesGivenCount = (int) likesGiven.stream()
                .filter(l -> l.getSession().getFecha().getYear() == year)
                .count();
        dto.setLikesGiven(likesGivenCount);

        // Likes recibidos
        List<Like> likesReceived = likeDao.findAllByLikedId(userId);
        int likesReceivedCount = (int) likesReceived.stream()
                .filter(l -> l.getSession().getFecha().getYear() == year)
                .count();
        dto.setLikesReceived(likesReceivedCount);

        // Comentarios recibidos
        List<Comment> commentsReceived = commentDao.findAllByCommentedId(userId);
        int commentsReceivedCount = (int) commentsReceived.stream()
                .filter(c -> {
                    if (c.getCreatedAt() == null)
                        return false;
                    LocalDateTime createdAt = LocalDateTime.ofInstant(c.getCreatedAt(), ZoneId.systemDefault());
                    return createdAt.getYear() == year;
                })
                .count();
        dto.setCommentsReceived(commentsReceivedCount);

        // Para comentarios dados necesitamos añadir un método al DAO
        dto.setCommentsGiven(0); // Por ahora
    }

    private void calculateTopInteraction(Long userId, int year, WrappedDto dto) {
        Map<User, Integer> interactions = new HashMap<>();

        // Contar likes dados a cada usuario
        List<Like> likesGiven = likeDao.findAllByLikerId(userId);
        for (Like like : likesGiven) {
            if (like.getSession().getFecha().getYear() == year) {
                User targetUser = like.getLiked();
                if (!targetUser.getId().equals(userId)) {
                    interactions.put(targetUser, interactions.getOrDefault(targetUser, 0) + 1);
                }
            }
        }

        // Contar likes recibidos de cada usuario
        List<Like> likesReceived = likeDao.findAllByLikedId(userId);
        for (Like like : likesReceived) {
            if (like.getSession().getFecha().getYear() == year) {
                User fromUser = like.getLiker();
                if (!fromUser.getId().equals(userId)) {
                    interactions.put(fromUser, interactions.getOrDefault(fromUser, 0) + 1);
                }
            }
        }

        Map.Entry<User, Integer> top = interactions.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (top != null) {
            dto.setTopInteractionUserName(top.getKey().getNombreUsuario());
            dto.setTopInteractionUserId(top.getKey().getId());
            dto.setTopInteractionCount(top.getValue());
        }
    }
}
