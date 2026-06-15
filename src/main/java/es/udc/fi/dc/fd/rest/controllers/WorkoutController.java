package es.udc.fi.dc.fd.rest.controllers;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.SetLog;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;
import es.udc.fi.dc.fd.model.services.SetLogService;
import es.udc.fi.dc.fd.model.services.WorkoutService;
import es.udc.fi.dc.fd.rest.dtos.ExerciseProgressDto;
import es.udc.fi.dc.fd.rest.dtos.ExerciseStatsDto;
import es.udc.fi.dc.fd.rest.dtos.RankingEntryDto;
import es.udc.fi.dc.fd.rest.dtos.SetLogConversor;
import es.udc.fi.dc.fd.rest.dtos.LogWorkoutRequestDto;
import es.udc.fi.dc.fd.rest.dtos.RoutineConversor;
import es.udc.fi.dc.fd.rest.dtos.SetLogDto;
import es.udc.fi.dc.fd.rest.dtos.RoutineStatsDto;
import es.udc.fi.dc.fd.rest.dtos.UserStatisticsDto;
import es.udc.fi.dc.fd.rest.dtos.WorkoutSessionDto;
import jakarta.servlet.http.HttpServletRequest;
import es.udc.fi.dc.fd.rest.dtos.WorkoutSessionConversor;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {
    // Controlador REST de entrenamientos: registro de sets, sesiones y dashboard de coach
    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private SetLogService setLogService;

    // Registra un set realizado dentro de una sesión
    @PostMapping("/log")
    @ResponseStatus(HttpStatus.CREATED)
    public void logWorkout(@RequestAttribute Long userId,
            @Validated @RequestBody LogWorkoutRequestDto request) throws InstanceNotFoundException {
        workoutService.logWorkout(userId, request);
    }

    // Obtiene las sesiones del usuario autenticado
    @GetMapping("/user-sessions")
    public List<WorkoutSessionDto> getUserSessions(HttpServletRequest request)
            throws InstanceNotFoundException, AccessDeniedException {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        return WorkoutSessionConversor.toWorkoutSessionDtos(workoutService.getWorkoutSessionsByUser(userId));
    }

    // Detalle de una sesión (sets), solo para el dueño o coach de la rutina
    @GetMapping("/{sessionId}")
    public List<SetLogDto> getWorkoutDetails(@PathVariable Long sessionId,
            HttpServletRequest request) throws AccessDeniedException {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null)
            throw new AccessDeniedException("User not authenticated");

        WorkoutSession session = workoutService.getWorkoutSessionById(sessionId);

        boolean isOwner = session != null && session.getUser().getId().equals(userId);
        boolean isCoachOfRoutine = session != null && session.getRoutine().getUser().getId().equals(userId);

        if (!isOwner && !isCoachOfRoutine) {
            throw new AccessDeniedException("Not your session or session does not exist");
        }

        List<SetLog> sets = setLogService.getSetsOfSession(sessionId);
        return sets.stream().map(SetLogConversor::toSetLogDto).toList();
    }

    // Dashboard: sesiones completadas de las rutinas del coach
    @GetMapping("/coach-dashboard")
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public List<WorkoutSessionDto> getCoachDashboard(@RequestAttribute Long userId) {
        List<WorkoutSession> sessions = workoutService.getCompletedWorkoutsForCoach(userId);
        return WorkoutSessionConversor.toWorkoutSessionDtos(sessions);
    }

    // Inicia una sesión de entrenamiento para una rutina
    @PostMapping("/start/{routineId}")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkoutSessionDto startWorkout(@RequestAttribute Long userId,
            @PathVariable Long routineId) throws InstanceNotFoundException {
        WorkoutSession session = workoutService.startWorkout(userId, routineId);
        return WorkoutSessionConversor.toWorkoutSessionDto(session);
    }

    // Finaliza una sesión de entrenamiento y guarda los datos
    @PostMapping("/finish/{sessionId}")
    @ResponseStatus(HttpStatus.OK)
    public WorkoutSessionDto finishWorkout(@RequestAttribute Long userId,
            @PathVariable Long sessionId,
            @Validated @RequestBody LogWorkoutRequestDto request)
            throws InstanceNotFoundException {
        WorkoutSession session = workoutService.finishWorkout(userId, sessionId, request);
        return WorkoutSessionConversor.toWorkoutSessionDto(session);
    }

    @GetMapping("/statistics")
    public UserStatisticsDto getUserStatistics(@RequestAttribute Long userId) throws InstanceNotFoundException {
        return workoutService.getUserStatistics(userId);
    }

    @GetMapping("/exercise-progress/{exerciseId}")
    public List<ExerciseProgressDto> getExerciseProgress(@RequestAttribute Long userId,
                                                          @PathVariable Long exerciseId) 
            throws InstanceNotFoundException {
        return workoutService.getExerciseProgress(userId, exerciseId);
    }

    @GetMapping("/exercises-with-weight")
    public List<ExerciseStatsDto> getExercisesWithWeight(@RequestAttribute Long userId) 
            throws InstanceNotFoundException {
        return workoutService.getExercisesWithWeight(userId);
    }

    @GetMapping("/routines-with-weight")
    public List<RoutineStatsDto> getRoutinesWithWeight(@RequestAttribute Long userId)
            throws InstanceNotFoundException {
        return workoutService.getRoutinesWithWeight(userId);
    }

    @GetMapping("/ranking/exercise/{exerciseId}")
    public List<RankingEntryDto> getExerciseRanking(@RequestAttribute Long userId,
                                                    @PathVariable Long exerciseId)
            throws InstanceNotFoundException {
        return workoutService.getExerciseRanking(userId, exerciseId);
    }

    @GetMapping("/ranking/routine/{routineId}")
    public List<RankingEntryDto> getRoutineRanking(@RequestAttribute Long userId,
                                                   @PathVariable Long routineId)
            throws InstanceNotFoundException {
        return workoutService.getRoutineRanking(userId, routineId);
    }
}