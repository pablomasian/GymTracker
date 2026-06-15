package es.udc.fi.dc.fd.model.services;

import java.util.List;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.rest.dtos.ExerciseProgressDto;
import es.udc.fi.dc.fd.rest.dtos.ExerciseStatsDto;
import es.udc.fi.dc.fd.rest.dtos.LogWorkoutRequestDto;
import es.udc.fi.dc.fd.rest.dtos.RankingEntryDto;
import es.udc.fi.dc.fd.rest.dtos.RoutineStatsDto;
import es.udc.fi.dc.fd.rest.dtos.UserStatisticsDto;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;

public interface WorkoutService {
    void logWorkout(Long userId, LogWorkoutRequestDto dto) throws InstanceNotFoundException;
    List <WorkoutSession> getWorkoutSessionsByUser (Long userId) throws InstanceNotFoundException;
    WorkoutSession getWorkoutSessionById (Long sessionId);
    List<WorkoutSession> getCompletedWorkoutsForCoach(Long coachId);
    WorkoutSession startWorkout(Long userId, Long routineId) throws InstanceNotFoundException;
    WorkoutSession finishWorkout(Long userId, Long sessionId, LogWorkoutRequestDto dto) throws InstanceNotFoundException;
    UserStatisticsDto getUserStatistics(Long userId) throws InstanceNotFoundException;
    List<WorkoutSession> getPublicWorkoutSessionsByUser(Long userId) throws InstanceNotFoundException;
    List<ExerciseProgressDto> getExerciseProgress(Long userId, Long exerciseId) throws InstanceNotFoundException;
    List<ExerciseStatsDto> getExercisesWithWeight(Long userId) throws InstanceNotFoundException;
    List<RankingEntryDto> getExerciseRanking(Long userId, Long exerciseId) throws InstanceNotFoundException;
    List<RankingEntryDto> getRoutineRanking(Long userId, Long routineId) throws InstanceNotFoundException;
    List<RoutineStatsDto> getRoutinesWithWeight(Long userId) throws InstanceNotFoundException;
}
