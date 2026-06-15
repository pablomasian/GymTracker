package es.udc.fi.dc.fd.rest.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.entities.Comment;
import es.udc.fi.dc.fd.model.entities.CommentDao;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.FollowerNotification;
import es.udc.fi.dc.fd.model.entities.FollowerNotificationDao;
import es.udc.fi.dc.fd.model.entities.Like;
import es.udc.fi.dc.fd.model.entities.LikeDao;
import es.udc.fi.dc.fd.model.entities.NotificationDao;
import es.udc.fi.dc.fd.model.entities.NotificationEndStreak;
import es.udc.fi.dc.fd.model.entities.NotificationEndStreakDao;
import es.udc.fi.dc.fd.model.entities.NotificationFollowedCoach;
import es.udc.fi.dc.fd.model.entities.NotificationFollowedCoachDao;
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
public class NotificationsControllerFullTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private NotificationEndStreakDao notificationEndStreakDao;

    @Autowired
    private NotificationFollowedCoachDao notificationFollowedCoachDao;

    @Autowired
    private FollowerNotificationDao followerNotificationDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private LikeDao likeDao;

    @Autowired
    private FollowDao followDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoutineDao routineDao;

    @Autowired
    private RoutineExerciseDao routineExerciseDao;

    @Autowired
    private SavedRoutineDao savedRoutineDao;

    @Autowired
    private SetLogDao setLogDao;

    @Autowired
    private WorkoutSessionDao workoutSessionDao;

    private User user;
    private User coach;

    @BeforeEach
    void setUp() throws Exception {
        // Borrar en orden de dependencias (tablas hijas primero)
        commentDao.deleteAll();
        likeDao.deleteAll();
        setLogDao.deleteAll();
        workoutSessionDao.deleteAll();
        notificationEndStreakDao.deleteAll();
        notificationFollowedCoachDao.deleteAll();
        followerNotificationDao.deleteAll();
        notificationDao.deleteAll();
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
    }

    @Test
    void testGetUnreadCount() throws Exception {
        mockMvc.perform(get("/api/notifications/unread-count")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists());
    }

    @Test
    void testGetStreakNotifications() throws Exception {
        mockMvc.perform(get("/api/notifications/streak")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetStreakUnreadCount() throws Exception {
        mockMvc.perform(get("/api/notifications/streak/unread-count")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists());
    }

    @Test
    void testMarkStreakNotificationRead() throws Exception {
        NotificationEndStreak notification = new NotificationEndStreak();
        notification.setUser(user);
        notification.setLeido(false);
        notification.setFechaCreacion(LocalDateTime.now());
        notification.setFechaLimite(LocalDateTime.now().plusDays(1));
        notification.setDiasRacha(5);
        notificationEndStreakDao.save(notification);

        mockMvc.perform(post("/api/notifications/streak/" + notification.getId() + "/read"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetFollowedCoachNotifications() throws Exception {
        mockMvc.perform(get("/api/notifications/followed-coach")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFollowedCoachUnreadCount() throws Exception {
        mockMvc.perform(get("/api/notifications/followed-coach/unread-count")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists());
    }

    @Test
    void testMarkCoachRoutineNotificationRead() throws Exception {
        NotificationFollowedCoach notification = new NotificationFollowedCoach();
        notification.setRoutineName("Test Routine");
        notification.setUser(user);
        notification.setCoach(coach);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationFollowedCoachDao.save(notification);

        mockMvc.perform(post("/api/notifications/followed-coach/" + notification.getId() + "/read")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetFollowerNotifications() throws Exception {
        mockMvc.perform(get("/api/notifications/followers")
                .requestAttr("userId", coach.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testMarkFollowerNotificationRead() throws Exception {
        FollowerNotification notification = new FollowerNotification();
        notification.setCoach(coach);
        notification.setFollower(user);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        followerNotificationDao.save(notification);

        mockMvc.perform(post("/api/notifications/followers/" + notification.getId() + "/read")
                .requestAttr("userId", coach.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetFollowerNotificationsUnreadCount() throws Exception {
        mockMvc.perform(get("/api/notifications/followers/unread-count")
                .requestAttr("userId", coach.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists());
    }

    @Test
    void testGetMyComments() throws Exception {
        mockMvc.perform(get("/api/notifications/my_comments")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCommentsUnreadCount() throws Exception {
        mockMvc.perform(get("/api/notifications/comments-unread-count")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists());
    }

    @Test
    void testMarkCommentRead() throws Exception {
        Routine routine = new Routine("Test Routine", coach);
        routineDao.save(routine);

        WorkoutSession session = new WorkoutSession(user, routine, LocalDateTime.now());
        workoutSessionDao.save(session);

        Comment comment = new Comment();
        comment.setCommenter(coach);
        comment.setCommented(user);
        comment.setSession(session);
        comment.setText("Great workout!");
        comment.setRead(false);
        comment.setCreatedAt(java.time.Instant.now());
        commentDao.save(comment);

        mockMvc.perform(post("/api/notifications/comments/" + comment.getId() + "/read")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetMyLikes() throws Exception {
        mockMvc.perform(get("/api/notifications/my_likes")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetLikesUnreadCount() throws Exception {
        mockMvc.perform(get("/api/notifications/likes-unread-count")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists());
    }

    @Test
    void testMarkLikeRead() throws Exception {
        Routine routine = new Routine("Test Routine", coach);
        routineDao.save(routine);

        WorkoutSession session = new WorkoutSession(user, routine, LocalDateTime.now());
        workoutSessionDao.save(session);

        Like like = new Like();
        like.setLiker(coach);
        like.setLiked(user);
        like.setSession(session);
        like.setRead(false);
        like.setCreatedAt(java.time.Instant.now());
        likeDao.save(like);

        mockMvc.perform(post("/api/notifications/likes/" + like.getId() + "/read")
                .requestAttr("userId", user.getId()))
                .andExpect(status().isNoContent());
    }
}
