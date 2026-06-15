package es.udc.fi.dc.fd.rest.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
import es.udc.fi.dc.fd.model.entities.WorkoutSession;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.model.services.UserService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class FeedControllerCoverageTest {

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

    private User user;
    private User coach;
    private Routine routine;
    private WorkoutSession session;

    @BeforeEach
    void setUp() throws Exception {
        commentDao.deleteAll();
        likeDao.deleteAll();
        setLogDao.deleteAll();
        workoutSessionDao.deleteAll();
        savedRoutineDao.deleteAll();
        routineExerciseDao.deleteAll();
        routineDao.deleteAll();
        followDao.deleteAll();
        userDao.deleteAll();

        userService.signUp(new User("user", "password", "Test", "User", "user"));
        user = userService.login("user", "password");

        userService.signUp(new User("coach", "password", "Coach", "User", "coach"));
        coach = userService.login("coach", "password");
        coach.setRole(RoleType.COACH);
        userDao.save(coach);

        // Create a routine
        routine = new Routine("Test Routine", coach);
        routine.setVisible(true);
        routine.setEstado(Routine.RoutineEstado.APPROVED);
        routineDao.save(routine);

        // Create a workout session
        session = new WorkoutSession(user, routine, LocalDateTime.now());
        workoutSessionDao.save(session);
    }

    @Test
    void testGetOwnAndFriendsWorkouts() throws Exception {
        mockMvc.perform(get("/api/feed/workouts")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testComment() throws Exception {
        mockMvc.perform(post("/api/feed/" + session.getId() + "/comment")
                .requestAttr("userId", user.getId())
                .param("text", "Great workout!"))
                .andExpect(status().isOk());
    }

    @Test
    void testLike() throws Exception {
        mockMvc.perform(post("/api/feed/like/" + session.getId())
                .requestAttr("userId", coach.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testUnlike() throws Exception {
        // First like
        mockMvc.perform(post("/api/feed/like/" + session.getId())
                .requestAttr("userId", coach.getId()));

        // Then unlike
        mockMvc.perform(post("/api/feed/unlike/" + session.getId())
                .requestAttr("userId", coach.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFeedWorkoutDetails() throws Exception {
        mockMvc.perform(get("/api/feed/session-feed-sets/" + session.getId())
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFeedWorkoutSetDetails() throws Exception {
        mockMvc.perform(get("/api/feed/session-feed-details/" + session.getId())
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSessionLikeCount() throws Exception {
        mockMvc.perform(get("/api/feed/likes-count/" + session.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSessionLikers() throws Exception {
        mockMvc.perform(get("/api/feed/session-likers/" + session.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSessionComments() throws Exception {
        mockMvc.perform(get("/api/feed/comments-of-session/" + session.getId()))
                .andExpect(status().isOk());
    }
}
