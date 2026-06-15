package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.Follow;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.NotificationDao;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.model.services.exceptions.MaxRoutinesExceededException;
import es.udc.fi.dc.fd.rest.dtos.ExerciseProgressDto;
import es.udc.fi.dc.fd.rest.dtos.ExerciseStatsDto;
import es.udc.fi.dc.fd.rest.dtos.LogSetDto;
import es.udc.fi.dc.fd.rest.dtos.LogWorkoutRequestDto;
import es.udc.fi.dc.fd.rest.dtos.RankingEntryDto;
import es.udc.fi.dc.fd.rest.dtos.RoutineStatsDto;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WorkoutServiceAdditionalTest {

    @Autowired
    private UserService userService;
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
    private NotificationDao notificationDao;

    private User user1;
    private User user2;
    private Exercise exercise;
    private Routine routine1;
    private Routine routine2;

    @Before
    public void setUp() throws Exception {
        setLogDao.deleteAll();
        workoutSessionDao.deleteAll();
        routineExerciseDao.deleteAll();
        followDao.deleteAll();
        notificationDao.deleteAll();

        user1 = createUser("user1_add");
        user2 = createUser("user2_add");
        exercise = exerciseDao.save(new Exercise("ExAdd", "Desc", "Muscle"));
        routine1 = routineService.createRoutine(user1.getId(), "Routine 1");
        routine2 = routineService.createRoutine(user2.getId(), "Routine 2");
    }

    private User createUser(String base) throws DuplicateInstanceException {
        User user = TestDataFactory.newUser(base);
        userService.signUp(user);
        return user;
    }

    private void logSession(User user, Routine routine, Exercise ex, int info, BigDecimal weight, LocalDateTime date)
            throws InstanceNotFoundException {
        LogSetDto set = new LogSetDto();
        set.setExerciseId(ex.getId());
        set.setReps(10);
        set.setWeight(weight != null ? weight : new BigDecimal(info));
        set.setSetNumber(1);

        LogWorkoutRequestDto req = new LogWorkoutRequestDto();
        req.setRoutineId(routine.getId());
        req.setDate(date);
        req.setDurationMinutes(60);
        req.setSets(List.of(set));

        workoutService.logWorkout(user.getId(), req);
    }

    @Test
    public void testGetExerciseProgress() throws InstanceNotFoundException {
        LocalDateTime now = LocalDateTime.now();
        logSession(user1, routine1, exercise, 10, new BigDecimal("50"), now.minusDays(2));
        logSession(user1, routine1, exercise, 10, new BigDecimal("60"), now.minusDays(1));

        List<ExerciseProgressDto> progress = workoutService.getExerciseProgress(user1.getId(), exercise.getId());

        assertEquals(2, progress.size());
        // Ordered by date? code says sorted by map key (date)
        assertEquals(new BigDecimal("50"), progress.get(0).getMaxWeight());
        assertEquals(new BigDecimal("60"), progress.get(1).getMaxWeight());
    }

    @Test
    public void testGetExercisesWithWeight() throws InstanceNotFoundException {
        logSession(user1, routine1, exercise, 10, new BigDecimal("100"), LocalDateTime.now());

        List<ExerciseStatsDto> stats = workoutService.getExercisesWithWeight(user1.getId());

        assertEquals(1, stats.size());
        assertEquals(exercise.getId(), stats.get(0).getExerciseId().longValue());
        assertEquals(10L, stats.get(0).getTotalReps()); // 10 reps * 1 set? no wait, sets logic
        // logic: totalSetsCount = sets.size(), totalRepsCount = sum(reps)
        // I logged 1 set of 10 reps.
        assertEquals(1L, stats.get(0).getTotalSets());
        assertEquals(10L, stats.get(0).getTotalReps());

        // Add another exercise
        Exercise ex2 = exerciseDao.save(new Exercise("Ex2", "D", "M"));
        logSession(user1, routine1, ex2, 20, new BigDecimal("50"), LocalDateTime.now());

        stats = workoutService.getExercisesWithWeight(user1.getId());
        assertEquals(2, stats.size());
    }

    @Test
    public void testGetRoutinesWithWeight() throws InstanceNotFoundException {
        logSession(user1, routine1, exercise, 10, new BigDecimal("100"), LocalDateTime.now());

        List<RoutineStatsDto> stats = workoutService.getRoutinesWithWeight(user1.getId());

        assertEquals(1, stats.size());
        assertEquals(routine1.getId(), stats.get(0).getRoutineId().longValue());
        // Weight = 100 * 10 = 1000
        assertEquals(new BigDecimal("1000"), stats.get(0).getTotalWeight());
    }

    @Test
    public void testGetCompletedWorkoutsForCoach() throws InstanceNotFoundException {
        logSession(user1, routine1, exercise, 10, null, LocalDateTime.now());

        List<WorkoutSession> sessions = workoutService.getCompletedWorkoutsForCoach(user1.getId());
        assertEquals(1, sessions.size());

        // user2 logs workout for routine2 (owned by user2)
        logSession(user2, routine2, exercise, 10, null, LocalDateTime.now());

        sessions = workoutService.getCompletedWorkoutsForCoach(user1.getId());
        assertEquals(1, sessions.size()); // Still 1 for user1
    }

    @Test
    public void testGetPublicWorkoutSessionsByUser() throws InstanceNotFoundException {
        logSession(user1, routine1, exercise, 10, null, LocalDateTime.now());
        List<WorkoutSession> sessions = workoutService.getPublicWorkoutSessionsByUser(user1.getId());
        assertEquals(1, sessions.size());
    }

    @Test
    public void testRankingWithFollows() throws InstanceNotFoundException {
        // User1 follows User2
        followDao.save(new Follow(user1, user2));

        // User2 lifts 100kg
        logSession(user2, routine2, exercise, 10, new BigDecimal("100"), LocalDateTime.now());

        // User1 checks ranking for exercise
        List<RankingEntryDto> ranking = workoutService.getExerciseRanking(user1.getId(), exercise.getId());

        // Should contain user2 and user1 (if user1 has data? no, logic says
        // participants = user + following)
        // User1 has no data yet.
        assertTrue(ranking.stream().anyMatch(r -> r.getUserId().equals(user2.getId())));

        // User1 lifts 50kg
        logSession(user1, routine1, exercise, 10, new BigDecimal("50"), LocalDateTime.now());

        ranking = workoutService.getExerciseRanking(user1.getId(), exercise.getId());
        assertEquals(2, ranking.size());
        // User2 (100) > User1 (50)
        assertEquals(user2.getId(), ranking.get(0).getUserId());
        assertEquals(user1.getId(), ranking.get(1).getUserId());
    }

    @Test
    public void testFinishWorkout_Notifications() throws InstanceNotFoundException {
        // User1 follows User2
        followDao.save(new Follow(user1, user2));

        // 1. User2 sets a record: 100kg
        WorkoutSession s2 = workoutService.startWorkout(user2.getId(), routine2.getId());
        LogSetDto set2 = new LogSetDto();
        set2.setExerciseId(exercise.getId());
        set2.setReps(10);
        set2.setWeight(new BigDecimal("100"));
        LogWorkoutRequestDto req2 = new LogWorkoutRequestDto();
        req2.setRoutineId(routine2.getId());
        req2.setDate(LocalDateTime.now());
        req2.setDurationMinutes(60);
        req2.setSets(List.of(set2));
        workoutService.finishWorkout(user2.getId(), s2.getId(), req2);

        // 2. User1 sets a record: 50kg (Rank: 1. User2, 2. User1)
        WorkoutSession s1 = workoutService.startWorkout(user1.getId(), routine1.getId());
        LogSetDto set1 = new LogSetDto();
        set1.setExerciseId(exercise.getId());
        set1.setReps(10);
        set1.setWeight(new BigDecimal("50"));
        LogWorkoutRequestDto req1 = new LogWorkoutRequestDto();
        req1.setRoutineId(routine1.getId());
        req1.setDate(LocalDateTime.now());
        req1.setDurationMinutes(60);
        req1.setSets(List.of(set1));
        workoutService.finishWorkout(user1.getId(), s1.getId(), req1);

        // Check notifications: User1 shouldn't get "overtook" yet because they just
        // entered?
        // Actually the logic diffs previous top 3 vs new top 3.
        // Before User1 finish: Top3 for User1 (participant set) -> {User2}
        // After User1 finish: Top3 -> {User2, User1}
        // User1 entered top 3.

        // 3. User1 improves to 150kg (Rank: 1. User1, 2. User2) -> Overtakes User2
        WorkoutSession s1b = workoutService.startWorkout(user1.getId(), routine1.getId());
        LogSetDto set1b = new LogSetDto();
        set1b.setExerciseId(exercise.getId());
        set1b.setReps(10);
        set1b.setWeight(new BigDecimal("150"));
        LogWorkoutRequestDto req1b = new LogWorkoutRequestDto();
        req1b.setRoutineId(routine1.getId());
        req1b.setDate(LocalDateTime.now());
        req1b.setDurationMinutes(60);
        req1b.setSets(List.of(set1b));

        workoutService.finishWorkout(user1.getId(), s1b.getId(), req1b);

        // Now User2 should get a notification that User1 passed them?
        // Logic:
        // Old Top3: User2 (1), User1 (2)
        // New Top3: User1 (1), User2 (2)
        // User1 moved 2->1. User2 moved 1->2.

        // Notifications?
        assertTrue(notificationDao.count() > 0);
    }

    @Test
    public void testGetRoutineRanking() throws InstanceNotFoundException {
        // User1 follows User2
        followDao.save(new Follow(user1, user2));

        // User2 logs a workout for routine2 (total weight: 10 * 10 = 100)
        logSession(user2, routine2, exercise, 10, new BigDecimal("10"), LocalDateTime.now());

        // User1 checks ranking for routine2
        // Logic: participants = User1 + User2.
        // User1 has 0. User2 has 100.
        List<RankingEntryDto> ranking = workoutService.getRoutineRanking(user1.getId(), routine2.getId());

        assertTrue(ranking.stream().anyMatch(r -> r.getUserId().equals(user2.getId())));
        RankingEntryDto user2Entry = ranking.stream().filter(r -> r.getUserId().equals(user2.getId())).findFirst()
                .get();
        // Weight calculation: 10 reps * 10 weight = 100.
        assertEquals(0, new BigDecimal("100").compareTo(user2Entry.getValue()));
    }

    @Test
    public void testGetWorkoutSessionsByUser() throws InstanceNotFoundException {
        logSession(user1, routine1, exercise, 10, null, LocalDateTime.now());
        List<WorkoutSession> sessions = workoutService.getWorkoutSessionsByUser(user1.getId());
        assertEquals(1, sessions.size());
    }
}
