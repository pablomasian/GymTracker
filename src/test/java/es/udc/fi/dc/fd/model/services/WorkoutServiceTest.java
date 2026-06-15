package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.services.exceptions.MaxRoutinesExceededException;
import es.udc.fi.dc.fd.model.entities.Badge;
import es.udc.fi.dc.fd.model.entities.Badge.BadgeType;
import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.rest.dtos.LogSetDto;
import es.udc.fi.dc.fd.rest.dtos.LogWorkoutRequestDto;
import es.udc.fi.dc.fd.rest.dtos.UserStatisticsDto;
import es.udc.fi.dc.fd.rest.dtos.ExerciseStatsDto;
import es.udc.fi.dc.fd.rest.dtos.RankingEntryDto;
import es.udc.fi.dc.fd.rest.dtos.RoutineStatsDto;
import es.udc.fi.dc.fd.model.entities.Follow;
import es.udc.fi.dc.fd.model.entities.FollowDao;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WorkoutServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private RoutineService routineService;

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private ExerciseDao exerciseDao;

    @Autowired
    private WorkoutSessionDao workoutSessionDao;

    @Autowired
    private SetLogDao setLogDao;

    @Autowired
    private RoutineExerciseDao routineExerciseDao;

    @Autowired
    private FollowDao followDao;
    
    @Autowired
    private es.udc.fi.dc.fd.model.entities.UserDao userDao;

    private User testUser;
    private Routine testRoutine;
    private Exercise squat;
    private Exercise benchPress;

    private User createUser(String base) throws DuplicateInstanceException {
        User user = TestDataFactory.newUser(base);
        userService.signUp(user);
        return user;
    }

    private Exercise createExercise(String name, String muscles) {
        return exerciseDao.save(new Exercise(name, "desc " + name, muscles));
    }
    
    private Routine createRoutine(User coach, String name) throws InstanceNotFoundException, MaxRoutinesExceededException {
        return routineService.createRoutine(coach.getId(), name);
    }

    private LogWorkoutRequestDto createLogWorkoutRequest(Long routineId, List<LogSetDto> sets, int duration, LocalDateTime date) {
        LogWorkoutRequestDto requestDto = new LogWorkoutRequestDto();
        requestDto.setRoutineId(routineId);
        requestDto.setDate(date);
        requestDto.setDurationMinutes(duration);
        requestDto.setSets(sets);
        return requestDto;
    }

    @Before
    public void setUp() throws DuplicateInstanceException, InstanceNotFoundException, MaxRoutinesExceededException {
        // Delete dependent tables first to avoid FK constraint violations
        setLogDao.deleteAll();
        workoutSessionDao.deleteAll();
        routineExerciseDao.deleteAll();
        followDao.deleteAll();
        
        testUser = createUser("testuser_workout");
        squat = createExercise("Squat", "Legs,Glutes");
        benchPress = createExercise("Bench Press", "Chest,Triceps");
        testRoutine = createRoutine(testUser, "Full Body Workout");
    }


    @Test
    public void testLogWorkout_Success() throws InstanceNotFoundException {
        LogSetDto set1 = new LogSetDto();
        set1.setExerciseId(squat.getId());
        set1.setReps(10);
        
        LogWorkoutRequestDto requestDto = createLogWorkoutRequest(testRoutine.getId(), List.of(set1), 55, LocalDateTime.now());
        workoutService.logWorkout(testUser.getId(), requestDto);

        List<WorkoutSession> sessions = workoutSessionDao.findAll();
        assertEquals(1, sessions.size());
        assertEquals(testUser.getId(), sessions.get(0).getUser().getId());
        
        long duration = ChronoUnit.MINUTES.between(sessions.get(0).getStartTime(), sessions.get(0).getEndTime());
        assertEquals(55, duration);
    }
    
    @Test
    public void testStartAndFinishWorkout_WithDuration() throws InstanceNotFoundException {
        WorkoutSession session = workoutService.startWorkout(testUser.getId(), testRoutine.getId());
        assertNotNull(session.getId());
        
        LogWorkoutRequestDto finishDto = createLogWorkoutRequest(testRoutine.getId(), Collections.emptyList(), 75, LocalDateTime.now());
        WorkoutSession finishedSession = workoutService.finishWorkout(testUser.getId(), session.getId(), finishDto);

        long durationInMinutes = ChronoUnit.MINUTES.between(finishedSession.getStartTime(), finishedSession.getEndTime());
        assertEquals(75, durationInMinutes);
    }

    @Test
    public void testGetUserStatistics_WithData() throws InstanceNotFoundException {
        LogSetDto squatSet = new LogSetDto();
        squatSet.setExerciseId(squat.getId());
        squatSet.setReps(10);
        squatSet.setWeight(new BigDecimal("100"));
        LogWorkoutRequestDto workout1 = createLogWorkoutRequest(testRoutine.getId(), List.of(squatSet), 60, LocalDateTime.now().minusDays(5));
        workoutService.logWorkout(testUser.getId(), workout1);
        
        LogSetDto benchSet = new LogSetDto();
        benchSet.setExerciseId(benchPress.getId());
        benchSet.setReps(8);
        benchSet.setWeight(new BigDecimal("80"));
        LogWorkoutRequestDto workout2 = createLogWorkoutRequest(testRoutine.getId(), List.of(benchSet), 40, LocalDateTime.now().minusDays(10));
        workoutService.logWorkout(testUser.getId(), workout2);
        
        UserStatisticsDto stats = workoutService.getUserStatistics(testUser.getId());

        assertEquals(2, stats.getTotalWorkouts());
        assertEquals(50.0, stats.getAverageDurationMinutes(), 0.1);
        assertEquals(0.5, stats.getWorkoutFrequency(), 0.01); 
    }

    @Test
    public void testGetUserStatistics_NoData() throws InstanceNotFoundException, DuplicateInstanceException {
        User newUser = createUser("nouser");
        
        UserStatisticsDto stats = workoutService.getUserStatistics(newUser.getId());

        assertEquals(0, stats.getTotalWorkouts());
        assertTrue(stats.getTopExercises().isEmpty());
    }

    @Test(expected = InstanceNotFoundException.class)
    public void testLogWorkout_RoutineNotFound() throws DuplicateInstanceException, InstanceNotFoundException {
        User user = createUser("testuser2");
        LogWorkoutRequestDto requestDto = createLogWorkoutRequest(999L, List.of(new LogSetDto()), 60, LocalDateTime.now());
        workoutService.logWorkout(user.getId(), requestDto);
    }

    @Test(expected = InstanceNotFoundException.class)
    public void testLogWorkout_UserNotFound() throws InstanceNotFoundException {
        LogWorkoutRequestDto requestDto = createLogWorkoutRequest(testRoutine.getId(), List.of(new LogSetDto()), 60, LocalDateTime.now());
        workoutService.logWorkout(999L, requestDto);
    }

    @Test(expected = InstanceNotFoundException.class)
    public void testLogWorkout_ExerciseNotFound() throws DuplicateInstanceException, InstanceNotFoundException, MaxRoutinesExceededException {
        User user = createUser("testuser3");
        Routine routine = createRoutine(user, "My Other Workout");
        
        LogSetDto setWithInvalidExercise = new LogSetDto();
        setWithInvalidExercise.setExerciseId(999L);
        LogWorkoutRequestDto requestDto = createLogWorkoutRequest(routine.getId(), List.of(setWithInvalidExercise), 60, LocalDateTime.now());
        
        workoutService.logWorkout(user.getId(), requestDto);
    }

    @Test
    public void testBadge_VolumeKing_200Reps() throws InstanceNotFoundException {
        // Crear sets con un total de 200 repeticiones
        LogSetDto set1 = new LogSetDto();
        set1.setExerciseId(squat.getId());
        set1.setReps(50);
        set1.setWeight(new BigDecimal("50"));
        
        LogSetDto set2 = new LogSetDto();
        set2.setExerciseId(benchPress.getId());
        set2.setReps(50);
        set2.setWeight(new BigDecimal("40"));
        
        LogSetDto set3 = new LogSetDto();
        set3.setExerciseId(squat.getId());
        set3.setReps(100);
        set3.setWeight(new BigDecimal("30"));
        
        WorkoutSession session = workoutService.startWorkout(testUser.getId(), testRoutine.getId());
        LogWorkoutRequestDto finishDto = createLogWorkoutRequest(testRoutine.getId(), List.of(set1, set2, set3), 60, LocalDateTime.now());
        workoutService.finishWorkout(testUser.getId(), session.getId(), finishDto);

        assertTrue(badgeService.hasBadge(testUser.getId(), BadgeType.VOLUME_KING));
        List<Badge> badges = badgeService.getBadgesByUser(testUser.getId());
        assertTrue(badges.stream().anyMatch(b -> b.getType() == BadgeType.VOLUME_KING));
    }

    @Test
    public void testBadge_EarlyBird_Before7AM() throws InstanceNotFoundException {
        LogSetDto set1 = new LogSetDto();
        set1.setExerciseId(squat.getId());
        set1.setReps(10);
        set1.setWeight(new BigDecimal("50"));
        
        // Simular un entrenamiento antes de las 7 AM
        LocalDateTime earlyMorning = LocalDateTime.now().withHour(6).withMinute(30);
        WorkoutSession session = workoutService.startWorkout(testUser.getId(), testRoutine.getId());
        session.setStartTime(earlyMorning);
        workoutSessionDao.save(session);
        
        LogWorkoutRequestDto finishDto = createLogWorkoutRequest(testRoutine.getId(), List.of(set1), 30, earlyMorning);
        workoutService.finishWorkout(testUser.getId(), session.getId(), finishDto);

        assertTrue(badgeService.hasBadge(testUser.getId(), BadgeType.EARLY_BIRD));
    }

    @Test
    public void testBadge_ConsistencyChampion_7DaysStreak() throws InstanceNotFoundException, DuplicateInstanceException, MaxRoutinesExceededException {
        User user = createUser("consistentUser");
        Routine routine = createRoutine(user, "Daily Routine");
        Exercise exercise = createExercise("Push-ups", "Chest");
        
        LogSetDto set = new LogSetDto();
        set.setExerciseId(exercise.getId());
        set.setReps(20);
        
        // Simular que el usuario ya tiene una racha de 6 días
        // El siguiente entrenamiento le dará el badge de 7 días
        user.setStreakCount(6);
        user.setLastTrainingDate(java.time.LocalDate.now().minusDays(1));
        userDao.save(user);
        
        LocalDateTime workoutDate = LocalDateTime.now();
        WorkoutSession session = workoutService.startWorkout(user.getId(), routine.getId());
        session.setStartTime(workoutDate);
        workoutSessionDao.save(session);
        
        LogWorkoutRequestDto finishDto = createLogWorkoutRequest(routine.getId(), List.of(set), 30, workoutDate);
        workoutService.finishWorkout(user.getId(), session.getId(), finishDto);

        // Ahora debería tener la badge de CONSISTENCY_CHAMPION (7 días)
        assertTrue(badgeService.hasBadge(user.getId(), BadgeType.CONSISTENCY_CHAMPION));
    }

    @Test
    public void testBadge_FiftyWorkouts() throws InstanceNotFoundException, DuplicateInstanceException, MaxRoutinesExceededException {
        User user = createUser("fiftyUser");
        Routine routine = createRoutine(user, "Regular Routine");
        Exercise exercise = createExercise("Squats", "Legs");
        
        LogSetDto set = new LogSetDto();
        set.setExerciseId(exercise.getId());
        set.setReps(10);
        set.setWeight(new BigDecimal("50"));
        
        // Registrar 50 entrenamientos
        for (int i = 0; i < 50; i++) {
            LocalDateTime workoutDate = LocalDateTime.now().minusDays(50 - i);
            WorkoutSession session = workoutService.startWorkout(user.getId(), routine.getId());
            session.setStartTime(workoutDate);
            workoutSessionDao.save(session);
            
            LogWorkoutRequestDto finishDto = createLogWorkoutRequest(routine.getId(), List.of(set), 30, workoutDate);
            workoutService.finishWorkout(user.getId(), session.getId(), finishDto);
        }

        assertTrue(badgeService.hasBadge(user.getId(), BadgeType.FIFTY_WORKOUTS));
        List<Badge> badges = badgeService.getBadgesByUser(user.getId());
        assertTrue(badges.stream().anyMatch(b -> b.getType() == BadgeType.FIFTY_WORKOUTS));
    }

    @Test
    public void testBadge_MultipleBadgesInOneWorkout() throws InstanceNotFoundException {
        // Test que verifica que se pueden obtener múltiples badges en un mismo workout
        LogSetDto set1 = new LogSetDto();
        set1.setExerciseId(squat.getId());
        set1.setReps(100);
        set1.setWeight(new BigDecimal("100")); // HUNDRED badge
        
        LogSetDto set2 = new LogSetDto();
        set2.setExerciseId(benchPress.getId());
        set2.setReps(100);
        set2.setWeight(new BigDecimal("80")); // Total 200 reps - VOLUME_KING badge
        
        LocalDateTime earlyMorning = LocalDateTime.now().withHour(6).withMinute(0);
        WorkoutSession session = workoutService.startWorkout(testUser.getId(), testRoutine.getId());
        session.setStartTime(earlyMorning);
        workoutSessionDao.save(session);
        
        LogWorkoutRequestDto finishDto = createLogWorkoutRequest(testRoutine.getId(), List.of(set1, set2), 45, earlyMorning);
        workoutService.finishWorkout(testUser.getId(), session.getId(), finishDto);

        // Debería tener HUNDRED, VOLUME_KING y EARLY_BIRD
        assertTrue(badgeService.hasBadge(testUser.getId(), BadgeType.HUNDRED));
        assertTrue(badgeService.hasBadge(testUser.getId(), BadgeType.VOLUME_KING));
        assertTrue(badgeService.hasBadge(testUser.getId(), BadgeType.EARLY_BIRD));
        
        List<Badge> badges = badgeService.getBadgesByUser(testUser.getId());
        assertEquals(3, badges.size());
    }
}