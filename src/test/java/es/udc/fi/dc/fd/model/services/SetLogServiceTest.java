package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;
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

import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineDao;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.SetLog;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SetLogServiceTest {

    @Autowired
    private SetLogService setLogService;

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoutineDao routineDao;
    @Autowired
    private ExerciseDao exerciseDao;
    @Autowired
    private WorkoutSessionDao sessionDao;
    @Autowired
    private SetLogDao setLogDao;
    @Autowired
    private RoutineExerciseDao routineExerciseDao;
    @Autowired
    private FollowDao followDao;

    private User user;
    private Routine routine;
    private Exercise exercise;
    private WorkoutSession session1;
    private WorkoutSession session2;

    @Before
    public void setUp() {
        // Cleanup order: children before parents to avoid FK violations
        setLogDao.deleteAll();
        sessionDao.deleteAll();
        routineExerciseDao.deleteAll();
        exerciseDao.deleteAll();
        routineDao.deleteAll();
        followDao.deleteAll();
        userDao.deleteAll();

        user = userDao.save(TestDataFactory.newUser("user"));
        routine = routineDao.save(new Routine("My Routine", user));
        exercise = exerciseDao.save(new Exercise("Push Up", "desc", "chest"));

        session1 = sessionDao.save(new WorkoutSession(user, routine, LocalDateTime.now()));
        session2 = sessionDao.save(new WorkoutSession(user, routine, LocalDateTime.now().minusDays(1)));

        setLogDao.save(new SetLog(session1, exercise, 1, 10, new BigDecimal("20.0")));
        setLogDao.save(new SetLog(session1, exercise, 2, 8, new BigDecimal("22.5")));
        setLogDao.save(new SetLog(session2, exercise, 1, 12, new BigDecimal("15.0")));
    }

    @Test
    public void testGetSetsOfSession() {
        List<SetLog> sets = setLogService.getSetsOfSession(session1.getId());
        assertEquals(2, sets.size());
        assertEquals(10, sets.get(0).getRepeticiones());
        assertEquals(8, sets.get(1).getRepeticiones());
    }

    @Test
    public void testGetSetsOfSession_Empty() {
        WorkoutSession emptySession = sessionDao.save(new WorkoutSession(user, routine, LocalDateTime.now()));
        List<SetLog> sets = setLogService.getSetsOfSession(emptySession.getId());
        assertTrue(sets.isEmpty());
    }
}