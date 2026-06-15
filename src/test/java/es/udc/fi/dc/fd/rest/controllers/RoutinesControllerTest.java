package es.udc.fi.dc.fd.rest.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineDao;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.model.services.TestDataFactory;
import es.udc.fi.dc.fd.rest.dtos.CreateRoutineExerciseRequest;
import es.udc.fi.dc.fd.rest.dtos.CreateRoutineRequest;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) 
@ActiveProfiles("test")
@Transactional
public class RoutinesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private RoutineDao routineDao;

    @Autowired
    private ExerciseDao exerciseDao;

    @Autowired
    private RoutineExerciseDao routineExerciseDao;

    @Autowired
    private WorkoutSessionDao workoutSessionDao;
    @Autowired
    private SetLogDao setLogDao;
    @Autowired
    private FollowDao followDao;

    private User coach1;
    private User coach2;
    private Exercise exercise1;
    private Routine routine1;

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

        coach1 = TestDataFactory.newUser("coach1");
        coach1.setRole(User.RoleType.COACH);
        userDao.save(coach1);

        coach2 = TestDataFactory.newUser("coach2");
        coach2.setRole(User.RoleType.COACH);
        userDao.save(coach2);

        exercise1 = new Exercise("Push Up", "desc", "chest");
        exerciseDao.save(exercise1);

        routine1 = new Routine("Coach 1 Routine", coach1);
    routine1.setEstado(es.udc.fi.dc.fd.model.entities.Routine.RoutineEstado.APPROVED);
    routineDao.save(routine1);
    }

    @Test
    public void testGetAllRoutines() throws Exception {
        mockMvc.perform(get("/api/routines/display_all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Coach 1 Routine"));
    }

    @Test
    public void testGetMyRoutines() throws Exception {
        mockMvc.perform(get("/api/routines/my-routines")
                .requestAttr("userId", coach1.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Coach 1 Routine"));
    }

    @Test
    public void testCreateRoutine() throws Exception {
        CreateRoutineRequest request = new CreateRoutineRequest();
        request.setName("New Full Body");
        
        List<CreateRoutineExerciseRequest> exercises = new ArrayList<>();
        CreateRoutineExerciseRequest exReq = new CreateRoutineExerciseRequest();
        exReq.setExerciseId(exercise1.getId());
        exReq.setSets(3);
        exReq.setRepetitions(12);
        exercises.add(exReq);
        request.setExercises(exercises);

        mockMvc.perform(post("/api/routines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .requestAttr("userId", coach1.getId()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("New Full Body"))
            .andExpect(jsonPath("$.exerciseCount").value(1));
    }

    @Test
    public void testUpdateOwnRoutine() throws Exception {
        CreateRoutineRequest request = new CreateRoutineRequest();
        request.setName("Updated Routine Name");
        request.setExercises(new ArrayList<>()); 

        mockMvc.perform(put("/api/routines/{id}", routine1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .requestAttr("userId", coach1.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Routine Name"));
    }

    @Test
    public void testDeleteOwnRoutine() throws Exception {
        mockMvc.perform(delete("/api/routines/{id}", routine1.getId())
                .requestAttr("userId", coach1.getId()))
            .andExpect(status().isNoContent());
    }
}