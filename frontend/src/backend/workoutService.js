import { appFetch, fetchConfig } from "./appFetch";

const workoutService = {
  logWorkout: (payload, onSuccess, onErrors) => 
    appFetch("/workouts/log", fetchConfig("POST", payload), onSuccess, onErrors),
    
  getUserWorkouts: (onSuccess, onErrors) =>
    appFetch("/workouts/user-sessions", fetchConfig("GET"), onSuccess, onErrors),
  
  getWorkoutDetails: (sessionId, onSuccess, onErrors) =>
    appFetch(`/workouts/${sessionId}`, fetchConfig("GET"), onSuccess, onErrors),
  
  getCoachDashboard: (onSuccess, onErrors) =>
    appFetch("/workouts/coach-dashboard", fetchConfig("GET"), onSuccess, onErrors),

  getUserStatistics: (onSuccess, onErrors) =>
    appFetch("/workouts/statistics", fetchConfig("GET"), onSuccess, onErrors),

  startWorkout: (routineId, onSuccess, onErrors) =>
    appFetch(`/workouts/start/${routineId}`, fetchConfig('POST'), onSuccess, onErrors),

  finishWorkout: (sessionId, payload, onSuccess, onErrors) =>
    appFetch(`/workouts/finish/${sessionId}`, fetchConfig("POST", payload), onSuccess, onErrors),

  getExerciseProgress: (exerciseId, onSuccess, onErrors) =>
    appFetch(`/workouts/exercise-progress/${exerciseId}`, fetchConfig("GET"), onSuccess, onErrors),

  getExercisesWithWeight: (onSuccess, onErrors) =>
    appFetch("/workouts/exercises-with-weight", fetchConfig("GET"), onSuccess, onErrors),

  getRoutinesWithWeight: (onSuccess, onErrors) =>
    appFetch("/workouts/routines-with-weight", fetchConfig("GET"), onSuccess, onErrors),

  getExerciseRanking: (exerciseId, onSuccess, onErrors) =>
    appFetch(`/workouts/ranking/exercise/${exerciseId}`, fetchConfig("GET"), onSuccess, onErrors),

  getRoutineRanking: (routineId, onSuccess, onErrors) =>
    appFetch(`/workouts/ranking/routine/${routineId}`, fetchConfig("GET"), onSuccess, onErrors),
};

export default workoutService;