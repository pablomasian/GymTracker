package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;

import jakarta.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineExercise;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoutineExerciseServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RoutineService routineService;

    @Autowired
    private RoutineExerciseService routineExerciseService;

    @Autowired
    private ExerciseDao exerciseDao; 

    private User createUser(String userName) throws DuplicateInstanceException {
        User user = new User(userName, "password", "firstName", "lastName", userName + "@mail.com");
        userService.signUp(user);
        return user;
    }

    private Exercise createExercise(String name) {
        Exercise e = new Exercise(name, "desc " + name, "muscles");
        return exerciseDao.save(e); 
    }

    @Test
    public void testAddAndFindExercisesByRoutine() throws Exception {
        User user = createUser("coach");
        Routine routine = routineService.createRoutine(user.getId(), "Leg day");
        Exercise squat = createExercise("Squat"); 

    routineExerciseService.addRoutineExercise(user, routine, squat, 4, 10, null);

        List<RoutineExercise> exercises = routineExerciseService.findExercisesByRoutine(routine);

        assertEquals(1, exercises.size());
        assertEquals("Squat", exercises.get(0).getExercise().getName());
        assertEquals(4, exercises.get(0).getSets());
        assertEquals(10, exercises.get(0).getRepetitions());
    }

    @Test
    public void testAddRoutineExerciseWrongUser() throws Exception {
        User coach = createUser("coach");
        User other = createUser("student");
        Routine routine = routineService.createRoutine(coach.getId(), "Push day");
        Exercise bench = createExercise("Bench press"); 

        assertThrows(PermissionException.class, () -> {
            routineExerciseService.addRoutineExercise(other, routine, bench, 3, 8, null);
        });
    }

    @Test
    public void testFindExercisesByRoutineWithNoExercises() throws Exception {
        User user = createUser("coach");
        Routine routine = routineService.createRoutine(user.getId(), "Empty routine");
        List<RoutineExercise> exercises = routineExerciseService.findExercisesByRoutine(routine);
        assertEquals(0, exercises.size());
    }

    @Test
    public void testAddMultipleExercisesToRoutine() throws Exception {
        User user = createUser("coach");
        Routine routine = routineService.createRoutine(user.getId(), "Full body");

        Exercise squat = createExercise("Squat");
        Exercise bench = createExercise("Bench press");

    routineExerciseService.addRoutineExercise(user, routine, squat, 4, 10, null);
    routineExerciseService.addRoutineExercise(user, routine, bench, 3, 8, null);

        List<RoutineExercise> exercises = routineExerciseService.findExercisesByRoutine(routine);

        assertEquals(2, exercises.size());
        assertEquals("Squat", exercises.get(0).getExercise().getName());
        assertEquals("Bench press", exercises.get(1).getExercise().getName());
    }

}
