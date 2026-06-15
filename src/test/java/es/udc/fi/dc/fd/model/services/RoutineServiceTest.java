package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.User.RoleType;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;
import es.udc.fi.dc.fd.model.services.exceptions.MaxRoutinesExceededException;
import es.udc.fi.dc.fd.model.services.exceptions.MaxExercisesPerRoutineExceededException;
import es.udc.fi.dc.fd.rest.dtos.CreateRoutineExerciseRequest;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoutineServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RoutineService routineService;
    
    @Autowired
    private RoutineExerciseDao routineExerciseDao;

    @Autowired
    private SetLogDao setLogDao;

    @Autowired
    private ExerciseDao exerciseDao;
    
    private User coach1;
    private User coach2;

    @Before
    public void setUp() throws DuplicateInstanceException {
        setLogDao.deleteAll();
        routineExerciseDao.deleteAll();
        exerciseDao.deleteAll();

        coach1 = createCoach("coach1");
        coach2 = createCoach("coach2");
    }

    private User createCoach(String base) throws DuplicateInstanceException {
        User user = TestDataFactory.newUser(base);
        user.setRole(RoleType.COACH);
        userService.signUp(user);
        return user;
    }

    private Exercise createExercise(String name) {
        return exerciseDao.save(new Exercise(name, "desc", "muscles"));
    }

    @Test
    public void testCreateAndFindRoutinesByCoach() throws Exception {
        Routine routine1 = routineService.createRoutine(coach1.getId(), "Leg day");
        Routine routine2 = routineService.createRoutine(coach1.getId(), "Push day");

        List<Routine> routines = routineService.findRoutinesByCoach(coach1.getId());

        assertEquals(2, routines.size());
        assertEquals(routine1.getName(), routines.get(0).getName());
        assertEquals(routine2.getName(), routines.get(1).getName());
    }

    @Test
    public void testFindRoutinesByNonExistingUser() {
    // Comportamiento actual: devolver lista vacía cuando el usuario no existe
        List<Routine> routines = routineService.findRoutinesByCoach(9999L);
        assertTrue(routines.isEmpty());
    }

    @Test
    public void testFindRoutinesForUserWithoutAnyRoutine() throws Exception {
        User noRoutineCoach = createCoach("noRoutineCoach");
        List<Routine> routines = routineService.findRoutinesByCoach(noRoutineCoach.getId());
        assertTrue(routines.isEmpty());
    }

    @Test
    public void testUpdateRoutine() throws DuplicateInstanceException, InstanceNotFoundException, PermissionException, MaxRoutinesExceededException, MaxExercisesPerRoutineExceededException {
        Exercise squat = createExercise("Squat");
        Routine routine = routineService.createRoutine(coach1.getId(), "Old Name");
        
        String newName = "Updated Leg Day";
        List<CreateRoutineExerciseRequest> newExercises = new ArrayList<>();
        CreateRoutineExerciseRequest newSquat = new CreateRoutineExerciseRequest();
        newSquat.setExerciseId(squat.getId());
        newSquat.setSets(5);
        newSquat.setRepetitions(5);
        newExercises.add(newSquat);

        routineService.updateRoutine(routine.getId(), coach1.getId(), newName, newExercises);

        Routine updatedRoutine = routineService.findById(routine.getId());
        assertEquals(newName, updatedRoutine.getName());
        assertEquals(1, routineExerciseDao.countByRoutine(updatedRoutine));
        assertEquals(squat.getId(), routineExerciseDao.findByRoutine(updatedRoutine).get(0).getExercise().getId());
    }

    @Test
    public void testUpdateRoutinePermissionDenied() throws DuplicateInstanceException, InstanceNotFoundException, MaxRoutinesExceededException {
        Routine routine = routineService.createRoutine(coach1.getId(), "Secret Routine");

        assertThrows(PermissionException.class, () -> {
            routineService.updateRoutine(routine.getId(), coach2.getId(), "Hacked Name", new ArrayList<>());
        });
    }

    @Test
    public void testDeleteRoutine() throws DuplicateInstanceException, InstanceNotFoundException, PermissionException, MaxRoutinesExceededException {
        Routine routine = routineService.createRoutine(coach1.getId(), "To Be Deleted");
        Long routineId = routine.getId();

        routineService.deleteRoutine(routineId, coach1.getId());

        assertThrows(InstanceNotFoundException.class, () -> {
            routineService.findById(routineId);
        });
    }

    @Test
    public void testDeleteRoutinePermissionDenied() throws DuplicateInstanceException, InstanceNotFoundException, MaxRoutinesExceededException {
        Routine routine = routineService.createRoutine(coach1.getId(), "Another Secret Routine");
        Long routineId = routine.getId();

        assertThrows(PermissionException.class, () -> {
            routineService.deleteRoutine(routineId, coach2.getId());
        });
    }
}