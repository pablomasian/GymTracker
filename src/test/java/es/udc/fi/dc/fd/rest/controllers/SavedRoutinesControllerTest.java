package es.udc.fi.dc.fd.rest.controllers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.entities.CommentDao;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.LikeDao;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineDao;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.SavedRoutineDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.User.RoleType;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.model.services.UserService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class SavedRoutinesControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserService userService;

        @Autowired
        private RoutineDao routineDao;

        @Autowired
        private SavedRoutineDao savedRoutineDao;

        @Autowired
        private UserDao userDao;

        @Autowired
        private RoutineExerciseDao routineExerciseDao;

        @Autowired
        private WorkoutSessionDao workoutSessionDao;

        @Autowired
        private SetLogDao setLogDao;

        @Autowired
        private CommentDao commentDao;

        @Autowired
        private LikeDao likeDao;

        @Autowired
        private FollowDao followDao;

        private User user;
        private User coach;
        private Routine routine;

        @BeforeEach
        void setUp() throws Exception {
                // Borrar en orden de dependencias (tablas hijas primero)
                commentDao.deleteAll();
                likeDao.deleteAll();
                setLogDao.deleteAll();
                workoutSessionDao.deleteAll();
                savedRoutineDao.deleteAll();
                routineExerciseDao.deleteAll();
                routineDao.deleteAll();
                followDao.deleteAll();
                userDao.deleteAll();

                userService.signUp(new User("coach", "password", "Coach", "User", "coach"));
                coach = userService.login("coach", "password");
                coach.setRole(RoleType.COACH);

                userService.signUp(new User("user", "password", "Test", "User", "user"));
                user = userService.login("user", "password");

                routine = new Routine("Test Routine", coach);
                routineDao.save(routine);
        }

        @Test
        void testSaveAndUnsaveRoutine() throws Exception {
                mockMvc.perform(post("/api/saved-routines/" + routine.getId())
                                .requestAttr("userId", user.getId()))
                                .andExpect(status().isCreated());

                assertTrue(savedRoutineDao.findByUserAndRoutine(user, routine).isPresent());

                mockMvc.perform(post("/api/saved-routines/" + routine.getId())
                                .requestAttr("userId", user.getId()))
                                .andExpect(status().isCreated());

                mockMvc.perform(delete("/api/saved-routines/" + routine.getId())
                                .requestAttr("userId", user.getId()))
                                .andExpect(status().isNoContent());

                assertFalse(savedRoutineDao.findByUserAndRoutine(user, routine).isPresent());
        }

        @Test
        void testGetMySavedRoutines() throws Exception {
                mockMvc.perform(get("/api/saved-routines")
                                .requestAttr("userId", user.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(0));

                mockMvc.perform(post("/api/saved-routines/" + routine.getId())
                                .requestAttr("userId", user.getId()));

                mockMvc.perform(get("/api/saved-routines")
                                .requestAttr("userId", user.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].name").value("Test Routine"));
        }

        @Test
        void testSaveRoutine_NotFound() throws Exception {
                long nonExistentRoutineId = 9999L;
                mockMvc.perform(post("/api/saved-routines/" + nonExistentRoutineId)
                                .requestAttr("userId", user.getId()))
                                .andExpect(status().isNotFound());
        }
}