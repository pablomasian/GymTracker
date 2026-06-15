package es.udc.fi.dc.fd.rest.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineDao;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.model.services.TestDataFactory;
import es.udc.fi.dc.fd.model.services.WorkoutService;
import es.udc.fi.dc.fd.rest.dtos.LogSetDto;
import es.udc.fi.dc.fd.rest.dtos.LogWorkoutRequestDto;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) 
@ActiveProfiles("test")
@Transactional
public class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoutineDao routineDao;

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
    private WorkoutService workoutService;

    private User user1;
    private User coach;
    private Routine routine1;
    private Exercise exercise1;
    private WorkoutSession session1; 

    @Before
    public void setUp() {
        // Cleanup order: children before parents to avoid FK violations
        setLogDao.deleteAll();
        workoutSessionDao.deleteAll();
        routineExerciseDao.deleteAll();
        routineDao.deleteAll();
        exerciseDao.deleteAll();
        followDao.deleteAll();
        userDao.deleteAll();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        user1 = TestDataFactory.newUser("user1");
        user1.setRole(User.RoleType.USER);
        userDao.save(user1);

        coach = TestDataFactory.newUser("coach");
        coach.setRole(User.RoleType.COACH);
        userDao.save(coach);

        routine1 = new Routine("Test Routine", coach);
        routineDao.save(routine1);

        exercise1 = new Exercise("Test Push-up", "desc", "chest");
        exerciseDao.save(exercise1);
        
        session1 = new WorkoutSession(user1, routine1, LocalDateTime.now().minusDays(1));
        workoutSessionDao.save(session1);
    }

    @Test
    public void testLogWorkout() throws Exception {
        LogWorkoutRequestDto request = new LogWorkoutRequestDto();
        request.setRoutineId(routine1.getId());
        request.setDate(LocalDateTime.now());
        request.setDurationMinutes(55);
        
        List<LogSetDto> sets = new ArrayList<>();
        LogSetDto setDto = new LogSetDto();
        setDto.setExerciseId(exercise1.getId());
        setDto.setSetNumber(1);
        setDto.setReps(10);
        setDto.setWeight(new BigDecimal("20.5"));
        sets.add(setDto);
        request.setSets(sets);

        mockMvc.perform(post("/api/workouts/log")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .requestAttr("userId", user1.getId()))
            .andExpect(status().isCreated());
    }

    @Test
    public void testGetUserSessions() throws Exception {
        mockMvc.perform(get("/api/workouts/user-sessions")
                .requestAttr("userId", user1.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].routineName").value("Test Routine"));
    }
    
    @Test
    public void testGetWorkoutDetails() throws Exception {
        mockMvc.perform(get("/api/workouts/{sessionId}", session1.getId())
                .requestAttr("userId", user1.getId()))
            .andExpect(status().isOk());
    }

    @Test
    public void testGetUserStatistics() throws Exception {
        workoutSessionDao.deleteAll();

        LogWorkoutRequestDto request = new LogWorkoutRequestDto();
        request.setRoutineId(routine1.getId());
        request.setDate(LocalDateTime.now().minusDays(3));
        request.setDurationMinutes(45);
        List<LogSetDto> sets = new ArrayList<>();
        LogSetDto setDto = new LogSetDto();
        setDto.setExerciseId(exercise1.getId());
        setDto.setSetNumber(1);
        setDto.setReps(10);
        setDto.setWeight(new BigDecimal("50"));
        sets.add(setDto);
        request.setSets(sets);
        workoutService.logWorkout(user1.getId(), request);

        mockMvc.perform(get("/api/workouts/statistics")
                .requestAttr("userId", user1.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalWorkouts").value(1))
            .andExpect(jsonPath("$.averageDurationMinutes").value(45.0))
            .andExpect(jsonPath("$.totalSets").value(1))
            .andExpect(jsonPath("$.totalReps").value(10))
            .andExpect(jsonPath("$.totalWeightLifted").value(500.00));
    }
}