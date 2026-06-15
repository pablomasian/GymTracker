package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.*;
import es.udc.fi.dc.fd.rest.dtos.ExerciseProgressDto;
import es.udc.fi.dc.fd.rest.dtos.ExerciseStatsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WorkoutServiceExerciseProgressTest {

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ExerciseDao exerciseDao;

    @Autowired
    private WorkoutSessionDao workoutSessionDao;

    @Autowired
    private SetLogDao setLogDao;

    @Autowired
    private RoutineDao routineDao;

    private User createTestUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setNombreUsuario("Test User");
        user.setPassword("password");
        user.setRole(User.RoleType.USER);
        return userDao.save(user);
    }

    private Exercise createTestExercise(String name) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setDescription("Test exercise");
        exercise.setMuscles("Test muscles");
        exercise.setEquipment("Barbell");
        exercise.setestado(Exercise.ExerciseEstado.APPROVED);
        return exerciseDao.save(exercise);
    }

    private Routine createTestRoutine(User user) {
        Routine routine = new Routine();
        routine.setName("Test Routine");
        routine.setUser(user);
        routine.setEstado(Routine.RoutineEstado.APPROVED);
        return routineDao.save(routine);
    }

    @Test
    public void testGetExerciseProgress_WithValidData() throws InstanceNotFoundException {
        // Crear datos de prueba
        User user = createTestUser();
        Exercise exercise = createTestExercise("Bench Press");
        Routine routine = createTestRoutine(user);

        // Crear 3 sesiones con progreso de peso
        LocalDateTime now = LocalDateTime.now();
        
        WorkoutSession session1 = new WorkoutSession();
        session1.setUser(user);
        session1.setRoutine(routine);
        session1.setFecha(now.minusDays(10));
        session1 = workoutSessionDao.save(session1);

        SetLog set1 = new SetLog();
        set1.setSession(session1);
        set1.setExercise(exercise);
        set1.setRepeticiones(10);
        set1.setPeso(new BigDecimal("60.0"));
        setLogDao.save(set1);

        SetLog set2 = new SetLog();
        set2.setSession(session1);
        set2.setExercise(exercise);
        set2.setRepeticiones(8);
        set2.setPeso(new BigDecimal("70.0"));
        setLogDao.save(set2);

        WorkoutSession session2 = new WorkoutSession();
        session2.setUser(user);
        session2.setRoutine(routine);
        session2.setFecha(now.minusDays(5));
        session2 = workoutSessionDao.save(session2);

        SetLog set3 = new SetLog();
        set3.setSession(session2);
        set3.setExercise(exercise);
        set3.setRepeticiones(10);
        set3.setPeso(new BigDecimal("75.0"));
        setLogDao.save(set3);

        WorkoutSession session3 = new WorkoutSession();
        session3.setUser(user);
        session3.setRoutine(routine);
        session3.setFecha(now.minusDays(2));
        session3 = workoutSessionDao.save(session3);

        SetLog set4 = new SetLog();
        set4.setSession(session3);
        set4.setExercise(exercise);
        set4.setRepeticiones(12);
        set4.setPeso(new BigDecimal("80.0"));
        setLogDao.save(set4);

        // Ejecutar el método
        List<ExerciseProgressDto> progress = workoutService.getExerciseProgress(user.getId(), exercise.getId());

        // Verificar resultados
        assertNotNull(progress);
        assertEquals(3, progress.size());

        // Verificar que están ordenados por fecha
        assertTrue(progress.get(0).getFecha().isBefore(progress.get(1).getFecha()));
        assertTrue(progress.get(1).getFecha().isBefore(progress.get(2).getFecha()));

        // Verificar peso máximo de cada sesión
        assertEquals(new BigDecimal("70.0"), progress.get(0).getMaxWeight());
        assertEquals(new BigDecimal("75.0"), progress.get(1).getMaxWeight());
        assertEquals(new BigDecimal("80.0"), progress.get(2).getMaxWeight());

        // Verificar totales por sesión
        assertEquals(18, progress.get(0).getTotalReps()); // 10 + 8
        assertEquals(2, progress.get(0).getTotalSets());
        assertEquals(10, progress.get(1).getTotalReps());
        assertEquals(1, progress.get(1).getTotalSets());
        assertEquals(12, progress.get(2).getTotalReps());
        assertEquals(1, progress.get(2).getTotalSets());
    }

    @Test
    public void testGetExerciseProgress_UserNotFound() {
        assertThrows(InstanceNotFoundException.class, () -> {
            workoutService.getExerciseProgress(99999L, 1L);
        });
    }

    @Test
    public void testGetExerciseProgress_ExerciseNotFound() throws InstanceNotFoundException {
        User user = createTestUser();
        
        assertThrows(InstanceNotFoundException.class, () -> {
            workoutService.getExerciseProgress(user.getId(), 99999L);
        });
    }

    @Test
    public void testGetExerciseProgress_NoData() throws InstanceNotFoundException {
        User user = createTestUser();
        Exercise exercise = createTestExercise("Squat");

        List<ExerciseProgressDto> progress = workoutService.getExerciseProgress(user.getId(), exercise.getId());

        assertNotNull(progress);
        assertTrue(progress.isEmpty());
    }

    @Test
    public void testGetExercisesWithWeight_WithValidData() throws InstanceNotFoundException {
        User user = createTestUser();
        Exercise exercise1 = createTestExercise("Bench Press");
        Exercise exercise2 = createTestExercise("Squat");
        Exercise exercise3 = createTestExercise("Pull-up"); // Sin peso
        Routine routine = createTestRoutine(user);

        LocalDateTime now = LocalDateTime.now();
        
        WorkoutSession session = new WorkoutSession();
        session.setUser(user);
        session.setRoutine(routine);
        session.setFecha(now.minusDays(5));
        session = workoutSessionDao.save(session);

        // Sets con peso
        SetLog set1 = new SetLog();
        set1.setSession(session);
        set1.setExercise(exercise1);
        set1.setRepeticiones(10);
        set1.setPeso(new BigDecimal("60.0"));
        setLogDao.save(set1);

        SetLog set2 = new SetLog();
        set2.setSession(session);
        set2.setExercise(exercise2);
        set2.setRepeticiones(8);
        set2.setPeso(new BigDecimal("100.0"));
        setLogDao.save(set2);

        // Set sin peso (bodyweight)
        SetLog set3 = new SetLog();
        set3.setSession(session);
        set3.setExercise(exercise3);
        set3.setRepeticiones(12);
        set3.setPeso(null);
        setLogDao.save(set3);

        // Ejecutar el método
        List<ExerciseStatsDto> exercisesWithWeight = workoutService.getExercisesWithWeight(user.getId());

        // Verificar resultados
        assertNotNull(exercisesWithWeight);
        assertEquals(2, exercisesWithWeight.size());

        // Verificar que están ordenados por total de sets (descendente)
        assertTrue(exercisesWithWeight.stream().anyMatch(e -> e.getExerciseName().equals("Bench Press")));
        assertTrue(exercisesWithWeight.stream().anyMatch(e -> e.getExerciseName().equals("Squat")));
        assertFalse(exercisesWithWeight.stream().anyMatch(e -> e.getExerciseName().equals("Pull-up")));
    }

    @Test
    public void testGetExercisesWithWeight_UserNotFound() {
        assertThrows(InstanceNotFoundException.class, () -> {
            workoutService.getExercisesWithWeight(99999L);
        });
    }

    @Test
    public void testGetExercisesWithWeight_NoWeightExercises() throws InstanceNotFoundException {
        User user = createTestUser();
        Exercise exercise = createTestExercise("Plank");
        Routine routine = createTestRoutine(user);

        WorkoutSession session = new WorkoutSession();
        session.setUser(user);
        session.setRoutine(routine);
        session.setFecha(LocalDateTime.now().minusDays(5));
        session = workoutSessionDao.save(session);

        // Solo ejercicios sin peso
        SetLog set = new SetLog();
        set.setSession(session);
        set.setExercise(exercise);
        set.setRepeticiones(60); // segundos
        set.setPeso(null);
        setLogDao.save(set);

        List<ExerciseStatsDto> exercisesWithWeight = workoutService.getExercisesWithWeight(user.getId());

        assertNotNull(exercisesWithWeight);
        assertTrue(exercisesWithWeight.isEmpty());
    }

    @Test
    public void testGetExercisesWithWeight_OnlyRecentData() throws InstanceNotFoundException {
        User user = createTestUser();
        Exercise exercise1 = createTestExercise("Bench Press");
        Exercise exercise2 = createTestExercise("Deadlift");
        Routine routine = createTestRoutine(user);

        LocalDateTime now = LocalDateTime.now();
        
        // Sesión reciente (dentro del último mes)
        WorkoutSession recentSession = new WorkoutSession();
        recentSession.setUser(user);
        recentSession.setRoutine(routine);
        recentSession.setFecha(now.minusDays(15));
        recentSession = workoutSessionDao.save(recentSession);

        SetLog recentSet = new SetLog();
        recentSet.setSession(recentSession);
        recentSet.setExercise(exercise1);
        recentSet.setRepeticiones(10);
        recentSet.setPeso(new BigDecimal("60.0"));
        setLogDao.save(recentSet);

        // Sesión antigua (más de un mes)
        WorkoutSession oldSession = new WorkoutSession();
        oldSession.setUser(user);
        oldSession.setRoutine(routine);
        oldSession.setFecha(now.minusMonths(2));
        oldSession = workoutSessionDao.save(oldSession);

        SetLog oldSet = new SetLog();
        oldSet.setSession(oldSession);
        oldSet.setExercise(exercise2);
        oldSet.setRepeticiones(8);
        oldSet.setPeso(new BigDecimal("120.0"));
        setLogDao.save(oldSet);

        List<ExerciseStatsDto> exercisesWithWeight = workoutService.getExercisesWithWeight(user.getId());

        // Solo debe incluir la sesión reciente
        assertNotNull(exercisesWithWeight);
        assertEquals(1, exercisesWithWeight.size());
        assertEquals("Bench Press", exercisesWithWeight.get(0).getExerciseName());
    }
}
