package es.udc.fi.dc.fd.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.RoutineDao;
import es.udc.fi.dc.fd.model.entities.FollowDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.Routine;

/**
 * Integration test for {@link SeedDemoDataRunner} verifying demo data is seeded
 * when NOT using the "test" profile. This test intentionally does NOT set an
 * @ActiveProfiles("test") annotation so the runner (profile !test) is active.
 */
@SpringBootTest
public class SeedDemoDataRunnerTest {

    @Autowired private UserDao userDao;
    @Autowired private RoutineDao routineDao;
    @Autowired private FollowDao followDao;
    @Autowired private WorkoutSessionDao workoutSessionDao;
    @Autowired private SetLogDao setLogDao;

    @Test
    void demoUsersAndDataSeeded() {
        // Users (updated to use real demo usernames)
        assertTrue(userDao.findByUsername("maria.garcia").isPresent(), "maria.garcia should be seeded");
        assertTrue(userDao.findByUsername("andres.sanchez").isPresent(), "andres.sanchez should be seeded");

        User coach1 = userDao.findByUsername("andres.sanchez").get();
        // Routines for andres.sanchez include "Full Body Beginner"
        boolean hasFullBody = routineDao.findByUser(coach1).stream()
            .anyMatch(r -> "Full Body Beginner".equalsIgnoreCase(r.getName()));
        assertTrue(hasFullBody, "Expected 'Full Body Beginner' routine for andres.sanchez");

        // Follow relationships (maria.garcia follows carlos.lopez and laura.fernandez)
        User user1 = userDao.findByUsername("maria.garcia").get();
        User user2 = userDao.findByUsername("carlos.lopez").get();
        User user3 = userDao.findByUsername("laura.fernandez").get();
        assertTrue(followDao.existsByFollowerIdAndCoachId(user1.getId(), user2.getId()), "maria.garcia should follow carlos.lopez");
        assertTrue(followDao.existsByFollowerIdAndCoachId(user1.getId(), user3.getId()), "maria.garcia should follow laura.fernandez");

        // Workout session and set logs exist
        assertFalse(workoutSessionDao.findAll().isEmpty(), "Expected at least one WorkoutSession");
        assertFalse(setLogDao.findAll().isEmpty(), "Expected at least one SetLog");

        // Basic sanity: at least one approved routine
        boolean hasApproved = routineDao.findAll().stream()
            .anyMatch(r -> r.getEstado() == Routine.RoutineEstado.APPROVED);
        assertTrue(hasApproved, "Should have an approved routine seeded");
    }
}
