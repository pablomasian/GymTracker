package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.*;
import es.udc.fi.dc.fd.model.entities.Badge.BadgeType;
import es.udc.fi.dc.fd.rest.dtos.ExerciseProgressDto;
import es.udc.fi.dc.fd.rest.dtos.ExerciseStatsDto;
import es.udc.fi.dc.fd.rest.dtos.LogSetDto;
import es.udc.fi.dc.fd.rest.dtos.LogWorkoutRequestDto;
import es.udc.fi.dc.fd.rest.dtos.RankingEntryDto;
import es.udc.fi.dc.fd.rest.dtos.RoutineStatsDto;
import es.udc.fi.dc.fd.rest.dtos.UserStatisticsDto;

import es.udc.fi.dc.fd.model.services.BadgeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.udc.fi.dc.fd.model.entities.Notification;
import es.udc.fi.dc.fd.model.entities.NotificationDao;

@Service
@Transactional
public class WorkoutServiceImpl implements WorkoutService {
    @Autowired
    private PermissionChecker permissionChecker;
    @Autowired
    private StreakService streakService;
    @Autowired
    private RoutineDao routineDao;
    @Autowired
    private ExerciseDao exerciseDao;
    @Autowired
    private WorkoutSessionDao workoutSessionDao;
    @Autowired
    private SetLogDao setLogDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private BadgeService badgeService;
    @Autowired
    private FollowService followService;
    @Autowired
    private NotificationDao notificationDao;

    // NO SE USA AHORA
    @Override
    public void logWorkout(Long userId, LogWorkoutRequestDto dto) throws InstanceNotFoundException {
        User user = permissionChecker.checkUser(userId);
        Routine routine = routineDao.findById(dto.getRoutineId())
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.routine", dto.getRoutineId()));

        WorkoutSession session = new WorkoutSession(user, routine, dto.getDate());

        session.setStartTime(dto.getDate());
        long duration = dto.getDurationMinutes() != null && dto.getDurationMinutes() > 0 ? dto.getDurationMinutes()
                : 60L;
        session.setEndTime(dto.getDate().plusMinutes(duration));
        workoutSessionDao.save(session);

        for (LogSetDto setDto : dto.getSets()) {
            Exercise exercise = exerciseDao.findById(setDto.getExerciseId())
                    .orElseThrow(
                            () -> new InstanceNotFoundException("project.entities.exercise", setDto.getExerciseId()));

            SetLog setLog = new SetLog(session, exercise, setDto.getSetNumber(), setDto.getReps(), setDto.getWeight());
            // Añadir campos de cardio si existen
            if (setDto.getDistance() != null) {
                setLog.setDistancia(setDto.getDistance());
            }
            if (setDto.getDuration() != null) {
                setLog.setDuracion(setDto.getDuration());
            }
            setLogDao.save(setLog);
        }
        streakService.registrarEntrenamiento(userId);
    }

    @Override
    public WorkoutSession startWorkout(Long userId, Long routineId) throws InstanceNotFoundException {
        User user = permissionChecker.checkUser(userId);
        Routine routine = routineDao.findById(routineId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.routine", routineId));

        WorkoutSession session = new WorkoutSession();
        session.setUser(user);
        session.setRoutine(routine);
        session.setFecha(LocalDateTime.now());
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(null);

        workoutSessionDao.save(session);
        return session;
    }

    @Override
    public WorkoutSession finishWorkout(Long userId, Long sessionId, LogWorkoutRequestDto dto)
            throws InstanceNotFoundException {
        User user = permissionChecker.checkUser(userId);
        WorkoutSession session = workoutSessionDao.findById(sessionId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.workoutSession", sessionId));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User not authorized to finish this session.");
        }

        long duration = dto.getDurationMinutes() != null && dto.getDurationMinutes() > 0 ? dto.getDurationMinutes()
                : ChronoUnit.MINUTES.between(session.getStartTime(), LocalDateTime.now());
        if (duration <= 0) {
            duration = 1; // Asegurar una duración mínima de 1 minuto
        }
        session.setEndTime(session.getStartTime().plusMinutes(duration));
        workoutSessionDao.save(session);

        int totalReps = 0;

        // collect distinct exercises updated in this workout
        List<Long> exerciseIds = dto.getSets().stream().map(LogSetDto::getExerciseId).distinct().collect(Collectors.toList());

        // capture previous top3s for relevant users (followers, following, and actor)
        Map<Long, Map<Long, List<Long>>> previousTop3 = new HashMap<>();
        // for each exercise, for each targetUserId store top3 list
        for (Long exerciseId : exerciseIds) {
            // targets: users that follow the actor
            List<User> followers = followService.getFollowersList(userId);
            for (User t : followers) {
                List<RankingEntryDto> ranking = getExerciseRanking(t.getId(), exerciseId);
                List<Long> top3 = ranking.stream().limit(3).map(RankingEntryDto::getUserId).collect(Collectors.toList());
                previousTop3.computeIfAbsent(exerciseId, k -> new HashMap<>()).put(t.getId(), top3);
            }

            // targets: users that the actor follows
            List<User> following = followService.getFollowingList(userId);
            for (User t : following) {
                List<RankingEntryDto> ranking = getExerciseRanking(t.getId(), exerciseId);
                List<Long> top3 = ranking.stream().limit(3).map(RankingEntryDto::getUserId).collect(Collectors.toList());
                previousTop3.computeIfAbsent(exerciseId, k -> new HashMap<>()).put(t.getId(), top3);
            }

            // also capture actor's own leaderboard
            List<RankingEntryDto> actorRanking = getExerciseRanking(userId, exerciseId);
            List<Long> actorTop3 = actorRanking.stream().limit(3).map(RankingEntryDto::getUserId).collect(Collectors.toList());
            previousTop3.computeIfAbsent(exerciseId, k -> new HashMap<>()).put(userId, actorTop3);
        }

        for (LogSetDto setDto : dto.getSets()) {
            Exercise exercise = exerciseDao.findById(setDto.getExerciseId())
                    .orElseThrow(
                            () -> new InstanceNotFoundException("project.entities.exercise", setDto.getExerciseId()));

            SetLog setLog = new SetLog(session, exercise, setDto.getSetNumber(), setDto.getReps(), setDto.getWeight());
            // Añadir campos de cardio si existen
            if (setDto.getDistance() != null) {
                setLog.setDistancia(setDto.getDistance());
            }
            if (setDto.getDuration() != null) {
                setLog.setDuracion(setDto.getDuration());
            }

            // Badge: HUNDRED - Levantar 100kg o más
            if ((setDto.getWeight() != null) && (setDto.getWeight().compareTo(new BigDecimal("100.00")) >= 0)
                    && (!badgeService.hasBadge(userId, BadgeType.HUNDRED))) {
                badgeService.giveBadge(userId, BadgeType.HUNDRED);
            }

            // Acumular repeticiones para badge VOLUME_KING
            totalReps += setDto.getReps();

            setLogDao.save(setLog);
        }

        // After saving, recompute top3s and create notifications for removed users (deduplicated)
        List<Notification> pending = new ArrayList<>();
        java.util.Set<String> seen = new java.util.HashSet<>();

        for (Long exerciseId : exerciseIds) {
            Map<Long, List<Long>> byTarget = previousTop3.getOrDefault(exerciseId, Collections.emptyMap());
            for (Map.Entry<Long, List<Long>> e : byTarget.entrySet()) {
                Long targetId = e.getKey();
                List<Long> oldTop3 = e.getValue();
                List<RankingEntryDto> newRanking = getExerciseRanking(targetId, exerciseId);
                List<Long> newTop3 = newRanking.stream().limit(3).map(RankingEntryDto::getUserId).collect(Collectors.toList());

                Map<Long, Integer> oldIndex = new HashMap<>();
                for (int i = 0; i < oldTop3.size(); i++) oldIndex.put(oldTop3.get(i), i);
                Map<Long, Integer> newIndex = new HashMap<>();
                for (int i = 0; i < newTop3.size(); i++) newIndex.put(newTop3.get(i), i);

                java.util.Set<Long> union = new java.util.HashSet<>();
                union.addAll(oldIndex.keySet());
                union.addAll(newIndex.keySet());

                for (Long uid : union) {
                    Integer oi = oldIndex.get(uid);
                    Integer ni = newIndex.get(uid);

                    if (oi != null && ni == null) {
                        Exercise ex = exerciseDao.findById(exerciseId).orElse(null);
                        if (targetId.equals(userId)) {
                            User actor = userDao.findById(userId).orElse(null);
                            User removed = userDao.findById(uid).orElse(null);
                            if (actor != null && removed != null && ex != null) {
                                String message = String.format("You removed your follower %s from your top %d in %s.",
                                        removed.getNombreUsuario() != null ? removed.getNombreUsuario() : removed.getUsername(), oi + 1, ex.getName());
                                String key = actor.getId() + "|" + message;
                                if (!seen.contains(key)) { seen.add(key); pending.add(new Notification(actor, "leaderboard", message)); }
                            }
                        } else {
                            User recipient = userDao.findById(uid).orElse(null);
                            User actor = userDao.findById(userId).orElse(null);
                            Exercise ex2 = exerciseDao.findById(exerciseId).orElse(null);
                            if (recipient != null && actor != null && ex2 != null) {
                                String message = String.format("%s removed you from the top %d in %s.",
                                        actor.getNombreUsuario() != null ? actor.getNombreUsuario() : actor.getUsername(), oi + 1, ex2.getName());
                                String key = recipient.getId() + "|" + message;
                                if (!seen.contains(key)) { seen.add(key); pending.add(new Notification(recipient, "leaderboard", message)); }
                            }
                        }
                    }

                    if (oi != null && ni != null && !oi.equals(ni)) {
                        if (oi < ni) {
                            User affected = userDao.findById(uid).orElse(null);
                            User actor = userDao.findById(userId).orElse(null);
                            Exercise ex3 = exerciseDao.findById(exerciseId).orElse(null);
                            if (affected != null && actor != null && ex3 != null) {
                                String message = String.format("You dropped from position %d to %d in %s (due to %s).",
                                        oi + 1, ni + 1, ex3.getName(), actor.getNombreUsuario() != null ? actor.getNombreUsuario() : actor.getUsername());
                                String key = affected.getId() + "|" + message;
                                if (!seen.contains(key)) { seen.add(key); pending.add(new Notification(affected, "leaderboard", message)); }
                            }
                            User actorUser = userDao.findById(userId).orElse(null);
                            User moved = userDao.findById(uid).orElse(null);
                            if (actorUser != null && moved != null && ex3 != null) {
                                String message2 = String.format("You moved %s from position %d to %d in %s.",
                                        moved.getNombreUsuario() != null ? moved.getNombreUsuario() : moved.getUsername(), oi + 1, ni + 1, ex3.getName());
                                String key2 = actorUser.getId() + "|" + message2;
                                if (!seen.contains(key2)) { seen.add(key2); pending.add(new Notification(actorUser, "leaderboard", message2)); }
                            }
                        }
                    }
                }
            }
        }

        // persist unique notifications
        for (Notification n : pending) notificationDao.save(n);

        // Badge: VOLUME_KING - Completar 200 o más repeticiones en un workout
        if (totalReps >= 200 && !badgeService.hasBadge(userId, BadgeType.VOLUME_KING)) {
            badgeService.giveBadge(userId, BadgeType.VOLUME_KING);
        }

        // Badge: EARLY_BIRD - Entrenar antes de las 7:00 AM
        if (session.getStartTime().getHour() < 7 && !badgeService.hasBadge(userId, BadgeType.EARLY_BIRD)) {
            badgeService.giveBadge(userId, BadgeType.EARLY_BIRD);
        }

        streakService.registrarEntrenamiento(userId);

        // Badge: CONSISTENCY_CHAMPION - Entrenar 7 días seguidos
        User userEntity = userDao.findById(userId).orElseThrow();
        if (userEntity.getStreakCount() >= 7 && !badgeService.hasBadge(userId, BadgeType.CONSISTENCY_CHAMPION)) {
            badgeService.giveBadge(userId, BadgeType.CONSISTENCY_CHAMPION);
        }

        // Badge: FIFTY_WORKOUTS - Completar 50 entrenamientos
        long totalWorkouts = workoutSessionDao.countByUser(userEntity);
        if (totalWorkouts >= 50 && !badgeService.hasBadge(userId, BadgeType.FIFTY_WORKOUTS)) {
            badgeService.giveBadge(userId, BadgeType.FIFTY_WORKOUTS);
        }

        return session;
    }

    @Override
    public List<WorkoutSession> getWorkoutSessionsByUser(Long userId) throws InstanceNotFoundException {
        User user = permissionChecker.checkUser(userId);
        return workoutSessionDao.findByUserOrderByFechaDesc(user);
    }

    @Override
    public WorkoutSession getWorkoutSessionById(Long sessionId) {
        return workoutSessionDao.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("WorkoutSession not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutSession> getCompletedWorkoutsForCoach(Long coachId) {
        return workoutSessionDao.findByRoutine_User_IdOrderByFechaDesc(coachId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatisticsDto getUserStatistics(Long userId) throws InstanceNotFoundException {
        if (!userDao.existsById(userId)) {
            throw new InstanceNotFoundException("project.entities.user", userId);
        }
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<WorkoutSession> sessions = workoutSessionDao.findByUserIdAndFechaAfterOrderByFechaDesc(userId,
                oneMonthAgo);

        if (sessions.isEmpty()) {
            return new UserStatisticsDto(0, 0, 0, 0, BigDecimal.ZERO, 0, "N/A", Collections.emptyMap(),
                    Collections.emptyList(), Arrays.asList(0L, 0L, 0L, 0L));
        }

        long totalWorkouts = sessions.size();
        double totalDurationMinutes = sessions.stream()
                .filter(s -> s.getStartTime() != null && s.getEndTime() != null)
                .mapToLong(s -> ChronoUnit.MINUTES.between(s.getStartTime(), s.getEndTime()))
                .sum();
        double averageDurationMinutes = totalWorkouts > 0 ? totalDurationMinutes / totalWorkouts : 0;

        List<SetLog> allSets = sessions.stream()
                .flatMap(session -> setLogDao.findBySessionId(session.getId()).stream())
                .collect(Collectors.toList());

        long totalSets = allSets.size();
        long totalReps = allSets.stream().mapToLong(SetLog::getRepeticiones).sum();
        BigDecimal totalWeightLifted = allSets.stream()
                .filter(s -> s.getPeso() != null)
                .map(s -> s.getPeso().multiply(new BigDecimal(s.getRepeticiones())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double workoutFrequency = (double) sessions.stream().map(s -> s.getFecha().toLocalDate()).distinct().count()
                / 4.0;

        String mostFrequentRoutine = sessions.stream()
                .collect(Collectors.groupingBy(s -> s.getRoutine().getName(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        Map<String, Long> muscleDistribution = allSets.stream()
                .map(set -> set.getExercise().getMuscles())
                .filter(muscles -> muscles != null && !muscles.isEmpty())
                .flatMap(muscles -> Stream.of(muscles.split(",")))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(muscle -> muscle, Collectors.counting()));

        // Agrupar por ejercicio para calcular stats
        Map<Exercise, List<SetLog>> exerciseGroups = allSets.stream()
                .collect(Collectors.groupingBy(SetLog::getExercise));

        List<ExerciseStatsDto> topExercises = exerciseGroups.entrySet().stream()
                .map(entry -> {
                    Exercise exercise = entry.getKey();
                    List<SetLog> sets = entry.getValue();
                    long totalSetsCount = sets.size();
                    long totalRepsCount = sets.stream()
                            .mapToInt(SetLog::getRepeticiones)
                            .sum();
                    return new ExerciseStatsDto(exercise.getId(), exercise.getName(), totalSetsCount, totalRepsCount);
                })
                .sorted((a, b) -> Long.compare(b.getTotalSets(), a.getTotalSets()))
                .limit(10)
                .collect(Collectors.toList());

        List<Long> workoutsPerWeek = new ArrayList<>(Collections.nCopies(4, 0L));
        LocalDateTime now = LocalDateTime.now();
        for (WorkoutSession session : sessions) {
            long weeksAgo = ChronoUnit.WEEKS.between(session.getFecha(), now);
            if (weeksAgo < 4) {
                int index = 3 - (int) weeksAgo;
                workoutsPerWeek.set(index, workoutsPerWeek.get(index) + 1);
            }
        }

        return new UserStatisticsDto(totalWorkouts, averageDurationMinutes, totalSets, totalReps, totalWeightLifted,
                workoutFrequency, mostFrequentRoutine, muscleDistribution, topExercises, workoutsPerWeek);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutSession> getPublicWorkoutSessionsByUser(Long userId) throws InstanceNotFoundException {
        // Solo buscamos el usuario, sin verificar permisos
        User user = userDao.findById(userId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.user", userId));

        // Devolvemos las rutinas ordenadas por fecha descendente
        return workoutSessionDao.findByUserOrderByFechaDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseProgressDto> getExerciseProgress(Long userId, Long exerciseId)
            throws InstanceNotFoundException {
        // Verificar que el usuario existe
        if (!userDao.existsById(userId)) {
            throw new InstanceNotFoundException("project.entities.user", userId);
        }

        // Verificar que el ejercicio existe
        if (!exerciseDao.existsById(exerciseId)) {
            throw new InstanceNotFoundException("project.entities.exercise", exerciseId);
        }

        // Obtener todos los sets del usuario para ese ejercicio
        List<SetLog> setLogs = setLogDao.findByUserIdAndExerciseIdOrderByDate(userId, exerciseId);

        // Agrupar por sesión (fecha) y calcular el peso máximo por sesión
        Map<LocalDateTime, List<SetLog>> sessionGroups = setLogs.stream()
                .collect(Collectors.groupingBy(sl -> sl.getSession().getFecha()));

        // Crear lista de progreso
        List<ExerciseProgressDto> progress = sessionGroups.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    LocalDateTime fecha = entry.getKey();
                    List<SetLog> sessionSets = entry.getValue();

                    // Calcular peso máximo de la sesión
                    BigDecimal maxWeight = sessionSets.stream()
                            .map(SetLog::getPeso)
                            .filter(peso -> peso != null)
                            .max(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);

                    // Calcular reps totales
                    int totalReps = sessionSets.stream()
                            .mapToInt(SetLog::getRepeticiones)
                            .sum();

                    // Número de sets
                    int totalSets = sessionSets.size();

                    return new ExerciseProgressDto(fecha, maxWeight, totalReps, totalSets);
                })
                .collect(Collectors.toList());

        return progress;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseStatsDto> getExercisesWithWeight(Long userId) throws InstanceNotFoundException {
        // Verificar que el usuario existe
        if (!userDao.existsById(userId)) {
            throw new InstanceNotFoundException("project.entities.user", userId);
        }

        // Obtener todas las sesiones del usuario del último mes
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<WorkoutSession> sessions = workoutSessionDao.findByUserIdAndFechaAfterOrderByFechaDesc(userId,
                oneMonthAgo);

        // Obtener todos los sets de esas sesiones
        List<SetLog> allSets = sessions.stream()
                .flatMap(session -> setLogDao.findBySessionId(session.getId()).stream())
                .collect(Collectors.toList());

        // Filtrar solo ejercicios que tienen peso registrado y agrupar
        Map<Exercise, List<SetLog>> exerciseGroups = allSets.stream()
                .filter(set -> set.getPeso() != null && set.getPeso().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.groupingBy(SetLog::getExercise));

        // Crear lista de ejercicios con sus estadísticas
        List<ExerciseStatsDto> exercisesWithWeight = exerciseGroups.entrySet().stream()
                .map(entry -> {
                    Exercise exercise = entry.getKey();
                    List<SetLog> sets = entry.getValue();
                    long totalSetsCount = sets.size();
                    long totalRepsCount = sets.stream()
                            .mapToInt(SetLog::getRepeticiones)
                            .sum();
                    return new ExerciseStatsDto(exercise.getId(), exercise.getName(), totalSetsCount, totalRepsCount);
                })
                .sorted((a, b) -> Long.compare(b.getTotalSets(), a.getTotalSets()))
                .collect(Collectors.toList());

        return exercisesWithWeight;
    }

    // Construye la lista de participantes: usuario actual + seguidos, sin
    // duplicados
    private List<User> buildParticipants(Long userId) throws InstanceNotFoundException {
        User current = userDao.findById(userId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.user", userId));

        List<User> participants = new ArrayList<>();
        participants.add(current);

        List<User> following = followService.getFollowingList(userId);
        if (following != null) {
            participants.addAll(following);
        }

        return participants.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(User::getId, u -> u, (a, b) -> a),
                        m -> new ArrayList<>(m.values())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankingEntryDto> getExerciseRanking(Long userId, Long exerciseId) throws InstanceNotFoundException {
        if (!exerciseDao.existsById(exerciseId)) {
            throw new InstanceNotFoundException("project.entities.exercise", exerciseId);
        }

        List<User> participants = buildParticipants(userId);

        return participants.stream()
                .map(u -> {
                    List<SetLog> logs = setLogDao.findByUserIdAndExerciseIdOrderByDate(u.getId(), exerciseId);
                    BigDecimal maxWeight = logs.stream()
                            .map(SetLog::getPeso)
                            .filter(p -> p != null)
                            .max(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);

                    String display = u.getNombreUsuario() != null ? u.getNombreUsuario() : u.getUsername();
                    return new RankingEntryDto(u.getId(), display, maxWeight);
                })
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankingEntryDto> getRoutineRanking(Long userId, Long routineId) throws InstanceNotFoundException {
        if (!routineDao.existsById(routineId)) {
            throw new InstanceNotFoundException("project.entities.routine", routineId);
        }

        List<User> participants = buildParticipants(userId);

        return participants.stream()
                .map(u -> {
                    List<WorkoutSession> sessions = workoutSessionDao.findByUserIdAndRoutineId(u.getId(), routineId);
                    BigDecimal totalWeight = sessions.stream()
                            .flatMap(s -> setLogDao.findBySessionId(s.getId()).stream())
                            .filter(sl -> sl.getPeso() != null)
                            .map(sl -> sl.getPeso().multiply(new BigDecimal(sl.getRepeticiones())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    String display = u.getNombreUsuario() != null ? u.getNombreUsuario() : u.getUsername();
                    return new RankingEntryDto(u.getId(), display, totalWeight);
                })
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoutineStatsDto> getRoutinesWithWeight(Long userId) throws InstanceNotFoundException {
        if (!userDao.existsById(userId)) {
            throw new InstanceNotFoundException("project.entities.user", userId);
        }

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<WorkoutSession> sessions = workoutSessionDao.findByUserIdAndFechaAfterOrderByFechaDesc(userId,
                oneMonthAgo);

        Map<Routine, BigDecimal> routineTotals = sessions.stream()
                .collect(Collectors.groupingBy(
                        WorkoutSession::getRoutine,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                sessList -> sessList.stream()
                                        .flatMap(s -> setLogDao.findBySessionId(s.getId()).stream())
                                        .filter(sl -> sl.getPeso() != null)
                                        .map(sl -> sl.getPeso().multiply(new BigDecimal(sl.getRepeticiones())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add))));

        return routineTotals.entrySet().stream()
                .map(entry -> new RoutineStatsDto(
                        entry.getKey().getId(),
                        entry.getKey().getName(),
                        entry.getValue()))
                .sorted((a, b) -> b.getTotalWeight().compareTo(a.getTotalWeight()))
                .collect(Collectors.toList());
    }

}