package es.udc.fi.dc.fd.rest.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.RoutineDao;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.model.services.TestDataFactory;
import es.udc.fi.dc.fd.rest.dtos.ExerciseDto;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) 
@ActiveProfiles("test")
@Transactional
public class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ExerciseDao exerciseDao;

    @Autowired
    private SetLogDao setLogDao;

    @Autowired
    private RoutineExerciseDao routineExerciseDao;

    @Autowired
    private FollowDao followDao;
    @Autowired
    private WorkoutSessionDao workoutSessionDao;
    @Autowired
    private RoutineDao routineDao;

    private User coach;
    private User regularUser;

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

        coach = TestDataFactory.newUser("coach");
        coach.setRole(User.RoleType.COACH);
        coach.setPremium(true);
        userDao.save(coach);

        regularUser = TestDataFactory.newUser("user");
        regularUser.setRole(User.RoleType.USER);
        userDao.save(regularUser);
    }

    @Test
    public void testListExercises() throws Exception {
        Exercise exercise = new Exercise("Push Up", "desc", "chest");
        exercise.setestado(Exercise.ExerciseEstado.APPROVED);
        exerciseDao.save(exercise);

        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Push Up"));
    }

    @Test
    public void testCreateExerciseAsCoach() throws Exception {
        ExerciseDto exerciseDto = new ExerciseDto(null, "New Exercise", "desc", "muscles", "equipment");

        mockMvc.perform(post("/api/exercises")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exerciseDto))
                .requestAttr("userId", coach.getId())) 
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Exercise"));
    }

    @Test
    public void testBlockExerciseAsAdmin() throws Exception {
        Exercise exercise = new Exercise("Test Exercise", "desc", "chest");
        exercise.setestado(Exercise.ExerciseEstado.APPROVED);
        Exercise saved = exerciseDao.save(exercise);

        mockMvc.perform(put("/api/exercises/" + saved.getId() + "/block")
                .requestAttr("userId", coach.getId()))
                .andExpect(status().isOk());
    }
}