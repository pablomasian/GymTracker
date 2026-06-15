package es.udc.fi.dc.fd.rest.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import es.udc.fi.dc.fd.model.entities.CommentDao;
import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.LikeDao;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineDao;
import es.udc.fi.dc.fd.model.entities.RoutineExercise;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.SavedRoutineDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.User.RoleType;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.model.services.UserService;
import es.udc.fi.dc.fd.rest.dtos.LogWorkoutRequestDto;
import es.udc.fi.dc.fd.rest.dtos.LogSetDto;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class WorkoutControllerCoverageTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoutineDao routineDao;

    @Autowired
    private RoutineExerciseDao routineExerciseDao;

    @Autowired
    private ExerciseDao exerciseDao;

    @Autowired
    private FollowDao followDao;

    @Autowired
    private SavedRoutineDao savedRoutineDao;

    @Autowired
    private WorkoutSessionDao workoutSessionDao;

    @Autowired
    private SetLogDao setLogDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private LikeDao likeDao;

    private ObjectMapper mapper;

    private User user;
    private User coach;
    private Routine routine;
    private Exercise exercise;
    private WorkoutSession session;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        commentDao.deleteAll();
        likeDao.deleteAll();
        setLogDao.deleteAll();
        workoutSessionDao.deleteAll();
        savedRoutineDao.deleteAll();
        routineExerciseDao.deleteAll();
        routineDao.deleteAll();
        exerciseDao.deleteAll();
        followDao.deleteAll();
        userDao.deleteAll();

        userService.signUp(new User("user", "password", "Test", "User", "user"));
        user = userService.login("user", "password");

        userService.signUp(new User("coach", "password", "Coach", "User", "coach"));
        coach = userService.login("coach", "password");
        coach.setRole(RoleType.COACH);
        userDao.save(coach);

        // Create exercise
        exercise = new Exercise();
        exercise.setName("Bench Press");
        exercise.setMuscles("Chest");
        exerciseDao.save(exercise);

        // Create a routine
        routine = new Routine("Test Routine", coach);
        routine.setVisible(true);
        routine.setEstado(Routine.RoutineEstado.APPROVED);
        routineDao.save(routine);

        // Add exercise to routine
        RoutineExercise re = new RoutineExercise();
        re.setRoutine(routine);
        re.setExercise(exercise);
        re.setSets(3);
        re.setRepetitions(10);
        re.setWeight(BigDecimal.valueOf(50.0));
        routineExerciseDao.save(re);

        // Create a workout session
        session = new WorkoutSession(user, routine, LocalDateTime.now());
        session.setStartTime(LocalDateTime.now());
        workoutSessionDao.save(session);
    }

    @Test
    void testGetUserSessions() throws Exception {
        mockMvc.perform(get("/api/workouts/user-sessions")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetWorkoutDetails() throws Exception {
        mockMvc.perform(get("/api/workouts/" + session.getId())
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCoachDashboard() throws Exception {
        mockMvc.perform(get("/api/workouts/coach-dashboard")
                .requestAttr("userId", coach.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testStartWorkout() throws Exception {
        mockMvc.perform(post("/api/workouts/start/" + routine.getId())
                .requestAttr("userId", user.getId()))
                .andExpect(status().isCreated());
    }

    @Test
    void testFinishWorkout() throws Exception {
        LogSetDto set = new LogSetDto();
        set.setExerciseId(exercise.getId());
        set.setSetNumber(1);
        set.setReps(10);
        set.setWeight(BigDecimal.valueOf(50.0));

        LogWorkoutRequestDto request = new LogWorkoutRequestDto();
        request.setRoutineId(routine.getId());
        request.setDate(LocalDateTime.now());
        request.setSets(List.of(set));

        mockMvc.perform(post("/api/workouts/finish/" + session.getId())
                .requestAttr("userId", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetUserStatistics() throws Exception {
        mockMvc.perform(get("/api/workouts/statistics")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetExerciseProgress() throws Exception {
        mockMvc.perform(get("/api/workouts/exercise-progress/" + exercise.getId())
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetExercisesWithWeight() throws Exception {
        mockMvc.perform(get("/api/workouts/exercises-with-weight")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoutinesWithWeight() throws Exception {
        mockMvc.perform(get("/api/workouts/routines-with-weight")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetExerciseRanking() throws Exception {
        mockMvc.perform(get("/api/workouts/ranking/exercise/" + exercise.getId())
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRoutineRanking() throws Exception {
        mockMvc.perform(get("/api/workouts/ranking/routine/" + routine.getId())
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }
}
