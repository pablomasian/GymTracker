package es.udc.fi.dc.fd.model.services;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.entities.*;
import es.udc.fi.dc.fd.rest.dtos.WrappedDto;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WrappedServiceTest {

    @Autowired
    private WrappedService wrappedService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ExerciseDao exerciseDao;

    @Autowired
    private RoutineDao routineDao;

    @Autowired
    private WorkoutSessionDao workoutSessionDao;

    @Autowired
    private SetLogDao setLogDao;

    @Autowired
    private FollowDao followDao;

    @Autowired
    private LikeDao likeDao;

    private User createUser(String username) {
        User user = new User();
        user.setNombreUsuario(username);
        user.setUsername(username);
        user.setPassword("password123");
        user.setRole(User.RoleType.USER);
        user.setBlocked(false);
        return userDao.save(user);
    }

    private User createCoach(String username) {
        User user = new User();
        user.setNombreUsuario(username);
        user.setUsername(username);
        user.setPassword("password123");
        user.setRole(User.RoleType.COACH);
        user.setBlocked(false);
        return userDao.save(user);
    }

    private Exercise createExercise(String name, String muscles) {
        Exercise exercise = new Exercise(name, "Description", muscles);
        exercise.setestado(Exercise.ExerciseEstado.APPROVED);
        return exerciseDao.save(exercise);
    }

    private Routine createRoutine(User coach, String name) {
        Routine routine = new Routine(name, coach);
        routine.setVisible(true);
        routine.setEstado(Routine.RoutineEstado.APPROVED);
        return routineDao.save(routine);
    }

    private WorkoutSession createSession(User user, Routine routine, LocalDateTime fecha) {
        WorkoutSession session = new WorkoutSession();
        session.setUser(user);
        session.setRoutine(routine);
        session.setFecha(fecha);
        return workoutSessionDao.save(session);
    }

    private SetLog createSetLog(WorkoutSession session, Exercise exercise, int reps, BigDecimal weight) {
        SetLog setLog = new SetLog(session, exercise, 1, reps, weight);
        return setLogDao.save(setLog);
    }

    @Test
    public void testGetWrapped_EmptyUser() {
        User user = createUser("testuser1");
        int year = LocalDate.now().getYear();

        WrappedDto wrapped = wrappedService.getWrapped(user.getId(), year);

        assertNotNull(wrapped);
        assertEquals(year, wrapped.getYear());
        assertEquals(0, wrapped.getTotalWorkouts());
        assertEquals("N/A", wrapped.getBestMonth());
        assertEquals(0, wrapped.getBestMonthWorkouts());
        assertEquals(BigDecimal.ZERO, wrapped.getTotalWeightLifted());
    }

    @Test
    public void testGetWrapped_WithWorkouts() {
        User user = createUser("testuser2");
        User coach = createCoach("testcoach1");
        Routine routine = createRoutine(coach, "Test Routine");
        Exercise exercise = createExercise("Bench Press", "Chest, Triceps");

        int year = LocalDate.now().getYear();
        LocalDateTime sessionDate = LocalDateTime.of(year, 6, 15, 10, 0);

        // Crear 3 sesiones de entrenamiento
        for (int i = 0; i < 3; i++) {
            WorkoutSession session = createSession(user, routine, sessionDate.plusDays(i * 7));
            createSetLog(session, exercise, 10, new BigDecimal("50"));
            createSetLog(session, exercise, 10, new BigDecimal("50"));
        }

        WrappedDto wrapped = wrappedService.getWrapped(user.getId(), year);

        assertNotNull(wrapped);
        assertEquals(year, wrapped.getYear());
        assertEquals(3, wrapped.getTotalWorkouts());
        assertNotNull(wrapped.getBestMonth());
        assertTrue(wrapped.getBestMonthWorkouts() > 0);

        // Peso total: 3 sesiones * 2 sets * 10 reps * 50kg = 3000kg
        assertEquals(new BigDecimal("3000"), wrapped.getTotalWeightLifted());
    }

    @Test
    public void testGetWrapped_TopExercises() {
        User user = createUser("testuser3");
        User coach = createCoach("testcoach2");
        Routine routine = createRoutine(coach, "Strength Routine");

        Exercise bench = createExercise("Bench Press", "Chest");
        Exercise squat = createExercise("Squat", "Legs");
        Exercise deadlift = createExercise("Deadlift", "Back");

        int year = LocalDate.now().getYear();
        LocalDateTime date = LocalDateTime.of(year, 3, 1, 10, 0);

        // Crear sesiones con diferentes ejercicios
        for (int i = 0; i < 5; i++) {
            WorkoutSession session = createSession(user, routine, date.plusDays(i));
            createSetLog(session, bench, 10, new BigDecimal("60")); // 5 veces
            if (i < 3) {
                createSetLog(session, squat, 8, new BigDecimal("80")); // 3 veces
            }
            if (i < 1) {
                createSetLog(session, deadlift, 5, new BigDecimal("100")); // 1 vez
            }
        }

        WrappedDto wrapped = wrappedService.getWrapped(user.getId(), year);

        assertNotNull(wrapped.getTopExercises());
        assertFalse(wrapped.getTopExercises().isEmpty());

        // El ejercicio más hecho debería ser Bench Press
        assertEquals("Bench Press", wrapped.getTopExercises().get(0).getExerciseName());
        assertEquals(5, wrapped.getTopExercises().get(0).getCount());
    }

    @Test
    public void testGetWrapped_TopMuscleGroup() {
        User user = createUser("testuser4");
        User coach = createCoach("testcoach3");
        Routine routine = createRoutine(coach, "Push Day");

        Exercise bench = createExercise("Bench Press", "Chest, Triceps");
        Exercise inclineBench = createExercise("Incline Bench", "Chest, Shoulders");

        int year = LocalDate.now().getYear();
        LocalDateTime date = LocalDateTime.of(year, 5, 1, 10, 0);

        WorkoutSession session = createSession(user, routine, date);
        createSetLog(session, bench, 10, new BigDecimal("60"));
        createSetLog(session, inclineBench, 10, new BigDecimal("50"));

        WrappedDto wrapped = wrappedService.getWrapped(user.getId(), year);

        // Chest aparece en ambos ejercicios, debería ser el top
        assertEquals("Chest", wrapped.getTopMuscleGroup());
        assertEquals(2, wrapped.getTopMuscleGroupCount());
    }

    

    @Test
    public void testGetWrapped_FriendsRanking_InTop3() {
        User user = createUser("testuser6");
        User friend1 = createUser("friend1");
        User friend2 = createUser("friend2");
        User coach = createCoach("coach4");

        Routine routine = createRoutine(coach, "Routine");
        Exercise exercise = createExercise("Ex", "Muscles");

        // El usuario sigue a friend1 y friend2
        Follow follow1 = new Follow(user, friend1);
        Follow follow2 = new Follow(user, friend2);
        followDao.save(follow1);
        followDao.save(follow2);

        int year = LocalDate.now().getYear();
        LocalDateTime date = LocalDateTime.of(year, 8, 1, 10, 0);

        // Usuario: 10 sesiones (más que sus amigos)
        for (int i = 0; i < 10; i++) {
            WorkoutSession session = createSession(user, routine, date.plusDays(i));
            createSetLog(session, exercise, 10, new BigDecimal("50"));
        }

        // Friend1: 5 sesiones
        for (int i = 0; i < 5; i++) {
            WorkoutSession session = createSession(friend1, routine, date.plusDays(i));
            createSetLog(session, exercise, 10, new BigDecimal("50"));
        }

        // Friend2: 3 sesiones
        for (int i = 0; i < 3; i++) {
            WorkoutSession session = createSession(friend2, routine, date.plusDays(i));
            createSetLog(session, exercise, 10, new BigDecimal("50"));
        }

        WrappedDto wrapped = wrappedService.getWrapped(user.getId(), year);

        // Usuario debería estar en primer lugar
        assertNotNull(wrapped.getFriendsRanking());
        assertEquals(1, wrapped.getFriendsRanking());
        assertEquals(2, wrapped.getTotalFriends());
    }

    @Test
    public void testGetWrapped_WeightComparison_Elephants() {
        User user = createUser("testuser7");
        User coach = createCoach("coach5");
        Routine routine = createRoutine(coach, "Heavy Routine");
        Exercise exercise = createExercise("Deadlift", "Back");

        int year = LocalDate.now().getYear();
        LocalDateTime date = LocalDateTime.of(year, 9, 1, 10, 0);

        // Crear suficiente peso para superar un elefante (5000kg)
        // 10 sesiones * 3 sets * 20 reps * 100kg = 60000kg
        for (int i = 0; i < 10; i++) {
            WorkoutSession session = createSession(user, routine, date.plusDays(i));
            createSetLog(session, exercise, 20, new BigDecimal("100"));
            createSetLog(session, exercise, 20, new BigDecimal("100"));
            createSetLog(session, exercise, 20, new BigDecimal("100"));
        }

        WrappedDto wrapped = wrappedService.getWrapped(user.getId(), year);

        // Debería mencionar elefantes
        assertTrue(wrapped.getWeightComparison().contains("elefante"));
    }

    @Test
    public void testGetWrapped_Streak() {
        User user = createUser("testuser8");
        user.setStreakCount(15);
        userDao.save(user);

        int year = LocalDate.now().getYear();

        WrappedDto wrapped = wrappedService.getWrapped(user.getId(), year);

        assertEquals(15, wrapped.getCurrentStreak());
        assertEquals(15, wrapped.getLongestStreak());
    }

    @Test
    public void testGetWrapped_DifferentYear() {
        User user = createUser("testuser9");
        User coach = createCoach("coach6");
        Routine routine = createRoutine(coach, "Routine");
        Exercise exercise = createExercise("Ex2", "Muscles");

        int currentYear = LocalDate.now().getYear();
        int previousYear = currentYear - 1;

        // Sesiones del año anterior
        LocalDateTime lastYearDate = LocalDateTime.of(previousYear, 6, 1, 10, 0);
        for (int i = 0; i < 5; i++) {
            WorkoutSession session = createSession(user, routine, lastYearDate.plusDays(i));
            createSetLog(session, exercise, 10, new BigDecimal("50"));
        }

        // Sesiones del año actual
        LocalDateTime thisYearDate = LocalDateTime.of(currentYear, 6, 1, 10, 0);
        for (int i = 0; i < 3; i++) {
            WorkoutSession session = createSession(user, routine, thisYearDate.plusDays(i));
            createSetLog(session, exercise, 10, new BigDecimal("50"));
        }

        // Verificar año anterior
        WrappedDto wrappedPrevious = wrappedService.getWrapped(user.getId(), previousYear);
        assertEquals(5, wrappedPrevious.getTotalWorkouts());

        // Verificar año actual
        WrappedDto wrappedCurrent = wrappedService.getWrapped(user.getId(), currentYear);
        assertEquals(3, wrappedCurrent.getTotalWorkouts());
    }
}
