package es.udc.fi.dc.fd.rest.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import es.udc.fi.dc.fd.model.entities.CommentDao;
import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.LikeDao;
import es.udc.fi.dc.fd.model.entities.RoutineDao;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.SavedRoutineDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.User.RoleType;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.model.services.UserService;
import es.udc.fi.dc.fd.rest.dtos.ExerciseDto;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class ExerciseControllerCoverageTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ExerciseDao exerciseDao;

    @Autowired
    private RoutineDao routineDao;

    @Autowired
    private RoutineExerciseDao routineExerciseDao;

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

    private ObjectMapper mapper = new ObjectMapper();

    private User coach;
    private User admin;
    private Exercise exercise;

    @BeforeEach
    void setUp() throws Exception {
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

        userService.signUp(new User("coach", "password", "Coach", "User", "coach"));
        coach = userService.login("coach", "password");
        coach.setRole(RoleType.COACH);
        coach.setPremium(true); // Premium coach can propose exercises
        userDao.save(coach);

        userService.signUp(new User("admin", "password", "Admin", "User", "admin"));
        admin = userService.login("admin", "password");
        admin.setRole(RoleType.ADMIN);
        userDao.save(admin);

        // Create an exercise
        exercise = new Exercise();
        exercise.setName("Test Exercise");
        exercise.setDescription("A test exercise");
        exercise.setMuscles("Chest");
        exercise.setEquipment("Barbell");
        exercise.setestado(Exercise.ExerciseEstado.APPROVED);
        exerciseDao.save(exercise);
    }

    @Test
    void testListExercises() throws Exception {
        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isOk());
    }

    @Test
    void testListAllForAdmin() throws Exception {
        mockMvc.perform(get("/api/exercises/all")
                .requestAttr("userId", admin.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testListPending() throws Exception {
        // Create a pending exercise
        Exercise pending = new Exercise();
        pending.setName("Pending Exercise");
        pending.setMuscles("Back");
        pending.setestado(Exercise.ExerciseEstado.PENDING);
        exerciseDao.save(pending);

        mockMvc.perform(get("/api/exercises/pending")
                .requestAttr("userId", admin.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testAcceptExercise() throws Exception {
        // Create a pending exercise
        Exercise pending = new Exercise();
        pending.setName("Pending Exercise 2");
        pending.setMuscles("Arms");
        pending.setestado(Exercise.ExerciseEstado.PENDING);
        exerciseDao.save(pending);

        mockMvc.perform(put("/api/exercises/" + pending.getId() + "/accept")
                .requestAttr("userId", admin.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteExercise() throws Exception {
        // Create a pending exercise to delete
        Exercise toDelete = new Exercise();
        toDelete.setName("To Delete Exercise");
        toDelete.setMuscles("Legs");
        toDelete.setestado(Exercise.ExerciseEstado.PENDING);
        exerciseDao.save(toDelete);

        mockMvc.perform(delete("/api/exercises/" + toDelete.getId() + "/dismiss")
                .requestAttr("userId", admin.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testBlockExercise() throws Exception {
        mockMvc.perform(put("/api/exercises/" + exercise.getId() + "/block")
                .requestAttr("userId", admin.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateExercise() throws Exception {
        ExerciseDto dto = new ExerciseDto();
        dto.setName("New Exercise From Test");
        dto.setDescription("New exercise description");
        dto.setMuscles("Shoulders");
        dto.setEquipment("Dumbbell");

        mockMvc.perform(post("/api/exercises")
                .requestAttr("userId", coach.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateExercise_NonPremiumCoach() throws Exception {
        // Make coach non-premium
        coach.setPremium(false);
        userDao.save(coach);

        ExerciseDto dto = new ExerciseDto();
        dto.setName("New Exercise From Non-Premium");
        dto.setMuscles("Abs");

        mockMvc.perform(post("/api/exercises")
                .requestAttr("userId", coach.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
