package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.Follow;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.rest.dtos.ExerciseStatsDto;
import es.udc.fi.dc.fd.rest.dtos.LogSetDto;
import es.udc.fi.dc.fd.rest.dtos.LogWorkoutRequestDto;
import es.udc.fi.dc.fd.rest.dtos.RankingEntryDto;
import es.udc.fi.dc.fd.rest.dtos.RoutineStatsDto;

/**
 * Integration tests for Ranking/Leaderboard functionality
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RankingServiceTest {

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

    private User user1;
    private User user2;
    private Routine routine1;
    private Exercise squat;

    @Before
    public void setUp() throws DuplicateInstanceException, InstanceNotFoundException, MaxRoutinesExceededException {
        setLogDao.deleteAll();
        workoutSessionDao.deleteAll();
        routineExerciseDao.deleteAll();
        followDao.deleteAll();

        // Create users
        user1 = TestDataFactory.newUser("rankingUser1");
        user1.setRole(User.RoleType.USER);
        userService.signUp(user1);

        user2 = TestDataFactory.newUser("rankingUser2");
        user2.setRole(User.RoleType.USER);
        userService.signUp(user2);

        // Create exercises
        squat = exerciseDao.save(new Exercise("Squat", "desc squat", "Legs,Glutes"));

        // Create routines
        routine1 = routineService.createRoutine(user1.getId(), "Full Body");
    }

    @Test
    public void testGetExercisesWithWeight_WithData() throws InstanceNotFoundException {
        // Log workout with weight
        LogSetDto set = new LogSetDto();
        set.setExerciseId(squat.getId());
        set.setReps(10);
        set.setWeight(new BigDecimal("100"));

        LogWorkoutRequestDto request = new LogWorkoutRequestDto();
        request.setRoutineId(routine1.getId());
        request.setDate(LocalDateTime.now());
        request.setDurationMinutes(60);
        request.setSets(List.of(set));

        workoutService.logWorkout(user1.getId(), request);

        // Get exercises with weight
        List<ExerciseStatsDto> result = workoutService.getExercisesWithWeight(user1.getId());

        assertNotNull(result);
        assertTrue(result.size() > 0);
        
        ExerciseStatsDto stats = result.stream()
            .filter(e -> e.getExerciseId().equals(squat.getId()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(stats);
        assertEquals("Squat", stats.getExerciseName());
        assertTrue(stats.getTotalReps() > 0);
        assertTrue(stats.getTotalSets() > 0);
    }

    @Test
    public void testGetRoutinesWithWeight_WithData() throws InstanceNotFoundException {
        // Log two sets with different exercises
        LogSetDto set1 = new LogSetDto();
        set1.setExerciseId(squat.getId());
        set1.setReps(10);
        set1.setWeight(new BigDecimal("100"));

        LogWorkoutRequestDto request = new LogWorkoutRequestDto();
        request.setRoutineId(routine1.getId());
        request.setDate(LocalDateTime.now());
        request.setDurationMinutes(60);
        request.setSets(List.of(set1));

        workoutService.logWorkout(user1.getId(), request);

        // Get routines with weight
        List<RoutineStatsDto> result = workoutService.getRoutinesWithWeight(user1.getId());

        assertNotNull(result);
        assertTrue(result.size() > 0);
        
        RoutineStatsDto stats = result.stream()
            .filter(r -> r.getRoutineId().equals(routine1.getId()))
            .findFirst()
            .orElse(null);
        
        assertNotNull(stats);
        assertEquals("Full Body", stats.getRoutineName());
        assertTrue(stats.getTotalWeight().doubleValue() > 0);
    }

    @Test
    public void testExerciseRanking_WithFollowed() throws InstanceNotFoundException, DuplicateInstanceException, MaxRoutinesExceededException {
        // User1 follows User2
        followDao.save(new Follow(user1, user2));

        // User1 logs squat with 100kg
        LogSetDto set1 = new LogSetDto();
        set1.setExerciseId(squat.getId());
        set1.setReps(10);
        set1.setWeight(new BigDecimal("100"));

        LogWorkoutRequestDto request1 = new LogWorkoutRequestDto();
        request1.setRoutineId(routine1.getId());
        request1.setDate(LocalDateTime.now());
        request1.setDurationMinutes(60);
        request1.setSets(List.of(set1));
        workoutService.logWorkout(user1.getId(), request1);

        // User2 logs squat with 150kg
        Routine routine2 = routineService.createRoutine(user2.getId(), "Leg Day");
        LogSetDto set2 = new LogSetDto();
        set2.setExerciseId(squat.getId());
        set2.setReps(5);
        set2.setWeight(new BigDecimal("150"));

        LogWorkoutRequestDto request2 = new LogWorkoutRequestDto();
        request2.setRoutineId(routine2.getId());
        request2.setDate(LocalDateTime.now());
        request2.setDurationMinutes(45);
        request2.setSets(List.of(set2));
        workoutService.logWorkout(user2.getId(), request2);

        // Get exercise ranking
        List<RankingEntryDto> ranking = workoutService.getExerciseRanking(user1.getId(), squat.getId());

        assertNotNull(ranking);
        assertEquals("Ranking should have 2 entries", 2, ranking.size());
        
        // Check order (highest weight first)
        assertEquals("First should be user2 with 150kg", user2.getId(), ranking.get(0).getUserId());
        assertEquals("First value should be 150", 0, 
                     new BigDecimal("150").compareTo(ranking.get(0).getValue().setScale(0)));
        
        assertEquals("Second should be user1 with 100kg", user1.getId(), ranking.get(1).getUserId());
        assertEquals("Second value should be 100", 0, 
                     new BigDecimal("100").compareTo(ranking.get(1).getValue().setScale(0)));
    }

    @Test
    public void testRoutineRanking_WithFollowed() throws InstanceNotFoundException, DuplicateInstanceException {
        // User1 follows User2
        followDao.save(new Follow(user1, user2));

        // User1 logs workout: 100kg * 10 = 1000 total
        LogSetDto set1 = new LogSetDto();
        set1.setExerciseId(squat.getId());
        set1.setReps(10);
        set1.setWeight(new BigDecimal("100"));

        LogWorkoutRequestDto request1 = new LogWorkoutRequestDto();
        request1.setRoutineId(routine1.getId());
        request1.setDate(LocalDateTime.now());
        request1.setDurationMinutes(60);
        request1.setSets(List.of(set1));
        workoutService.logWorkout(user1.getId(), request1);

        // User2 logs same routine: 150kg * 10 = 1500 total
        LogSetDto set2 = new LogSetDto();
        set2.setExerciseId(squat.getId());
        set2.setReps(10);
        set2.setWeight(new BigDecimal("150"));

        LogWorkoutRequestDto request2 = new LogWorkoutRequestDto();
        request2.setRoutineId(routine1.getId());
        request2.setDate(LocalDateTime.now());
        request2.setDurationMinutes(60);
        request2.setSets(List.of(set2));
        workoutService.logWorkout(user2.getId(), request2);

        // Get routine ranking
        List<RankingEntryDto> ranking = workoutService.getRoutineRanking(user1.getId(), routine1.getId());

        assertNotNull(ranking);
        assertEquals("Ranking should have 2 entries", 2, ranking.size());
        
        // Check order (highest weight first)
        assertEquals("First should be user2 with 1500kg", user2.getId(), ranking.get(0).getUserId());
        assertEquals("Second should be user1 with 1000kg", user1.getId(), ranking.get(1).getUserId());
    }

    @Test
    public void testExerciseRanking_UserNotFound() throws InstanceNotFoundException {
        // Try to get ranking for non-existent user
        try {
            workoutService.getExerciseRanking(999L, squat.getId());
            assertTrue("Should throw exception for invalid user", false);
        } catch (InstanceNotFoundException e) {
            // Expected
            assertTrue(true);
        }
    }

    @Test
    public void testRoutineRanking_UserNotFound() throws InstanceNotFoundException {
        // Try to get ranking for non-existent user
        try {
            workoutService.getRoutineRanking(999L, routine1.getId());
            assertTrue("Should throw exception for invalid user", false);
        } catch (InstanceNotFoundException e) {
            // Expected
            assertTrue(true);
        }
    }

    @Test
    public void testGetExercisesWithWeight_UserNotFound() throws InstanceNotFoundException {
        try {
            workoutService.getExercisesWithWeight(999L);
            assertTrue("Should throw exception for invalid user", false);
        } catch (InstanceNotFoundException e) {
            // Expected
            assertTrue(true);
        }
    }

    @Test
    public void testGetRoutinesWithWeight_UserNotFound() throws InstanceNotFoundException {
        try {
            workoutService.getRoutinesWithWeight(999L);
            assertTrue("Should throw exception for invalid user", false);
        } catch (InstanceNotFoundException e) {
            // Expected
            assertTrue(true);
        }
    }
}
