package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

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
import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.Exercise.ExerciseEstado;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ExerciseServiceTest {

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private ExerciseDao exerciseDao;

    // Added to allow safe cleanup without FK violations from previous tests
    @Autowired
    private SetLogDao setLogDao;
    @Autowired
    private RoutineExerciseDao routineExerciseDao;

    @Before
    public void setUp() {
        // Delete dependent entities first to avoid referential integrity errors when
        // removing exercises created by earlier test classes.
        setLogDao.deleteAll();
        routineExerciseDao.deleteAll();
        exerciseDao.deleteAll();
    }

    private Exercise createExerciseEntity(String name, String description, String muscles) {
        return new Exercise(name, description, muscles);
    }

    @Test
    public void testCreateExercise() throws DuplicateInstanceException {
        Exercise newExercise = createExerciseEntity("Dumbbell Flyes", "Chest exercise", "Chest, Shoulders");

        Exercise createdExercise = exerciseService.createExercise(newExercise);

        assertNotNull(createdExercise.getId());
        assertEquals("Dumbbell Flyes", createdExercise.getName());
        assertEquals(ExerciseEstado.PENDING, createdExercise.getestado());
    }

    @Test
    public void testCreateExerciseWithDuplicateName() {
        Exercise existingExercise = createExerciseEntity("Push Ups", "Classic push up", "Chest");
        exerciseDao.save(existingExercise);

        Exercise duplicateExercise = createExerciseEntity("Push Ups", "Another description", "Chest, Triceps");

        assertThrows(DuplicateInstanceException.class, () -> {
            exerciseService.createExercise(duplicateExercise);
        });
    }

    @Test
    public void testListAllReturnsOnlyApproved() {
        Exercise approvedExercise = createExerciseEntity("Approved Squat", "desc", "legs");
        approvedExercise.setestado(ExerciseEstado.APPROVED);
        exerciseDao.save(approvedExercise);

        Exercise pendingExercise = createExerciseEntity("Pending Deadlift", "desc", "back");
        pendingExercise.setestado(ExerciseEstado.PENDING);
        exerciseDao.save(pendingExercise);

        List<Exercise> catalog = exerciseService.listAll();

        assertEquals(1, catalog.size());
        assertEquals("Approved Squat", catalog.get(0).getName());
    }

    @Test
    public void testListAllWithNoApprovedExercises() {
        Exercise pendingExercise = createExerciseEntity("Pending Row", "desc", "back");
        pendingExercise.setestado(ExerciseEstado.PENDING);
        exerciseDao.save(pendingExercise);

        List<Exercise> catalog = exerciseService.listAll();

        assertTrue(catalog.isEmpty());
    }

    @Test
    public void testBlockExercise() {
        Exercise exercise = createExerciseEntity("Bench Press", "Classic bench press", "Chest");
        exercise.setestado(ExerciseEstado.APPROVED);
        Exercise saved = exerciseDao.save(exercise);

        exerciseService.block(saved.getId());

        Exercise blocked = exerciseDao.findById(saved.getId()).orElseThrow();
        assertTrue(blocked.isBlocked());
    }

    @Test
    public void testListAllDoesNotReturnBlockedExercises() {
        Exercise approved = createExerciseEntity("Squat", "desc", "legs");
        approved.setestado(ExerciseEstado.APPROVED);
        approved.setBlocked(false);
        exerciseDao.save(approved);

        Exercise blockedExercise = createExerciseEntity("Blocked Press", "desc", "chest");
        blockedExercise.setestado(ExerciseEstado.APPROVED);
        blockedExercise.setBlocked(true);
        exerciseDao.save(blockedExercise);

        List<Exercise> catalog = exerciseService.listAll();

        assertEquals(1, catalog.size());
        assertEquals("Squat", catalog.get(0).getName());
    }

    @Test
    public void testCreateCardioExercise() throws DuplicateInstanceException {
        Exercise cardioExercise = createExerciseEntity("Running Test", "Cardio exercise", "Legs, Cardio");
        cardioExercise.setExerciseType(Exercise.ExerciseType.CARDIO);

        Exercise created = exerciseService.createExercise(cardioExercise);

        assertNotNull(created.getId());
        assertEquals("Running Test", created.getName());
        assertEquals(Exercise.ExerciseType.CARDIO, created.getExerciseType());
    }

    @Test
    public void testExerciseDefaultTypeIsStrength() throws DuplicateInstanceException {
        Exercise strengthExercise = createExerciseEntity("New Bench Press", "Strength exercise", "Chest");

        Exercise created = exerciseService.createExercise(strengthExercise);

        assertNotNull(created.getId());
        assertEquals(Exercise.ExerciseType.STRENGTH, created.getExerciseType());
    }

    @Test
    public void testListAllReturnsCardioExercises() {
        Exercise cardioExercise = createExerciseEntity("Cycling Test", "desc", "Cardio");
        cardioExercise.setestado(ExerciseEstado.APPROVED);
        cardioExercise.setExerciseType(Exercise.ExerciseType.CARDIO);
        exerciseDao.save(cardioExercise);

        Exercise strengthExercise = createExerciseEntity("Deadlift Test", "desc", "Back");
        strengthExercise.setestado(ExerciseEstado.APPROVED);
        strengthExercise.setExerciseType(Exercise.ExerciseType.STRENGTH);
        exerciseDao.save(strengthExercise);

        List<Exercise> catalog = exerciseService.listAll();

        assertEquals(2, catalog.size());
        assertTrue(catalog.stream().anyMatch(e -> e.getExerciseType() == Exercise.ExerciseType.CARDIO));
        assertTrue(catalog.stream().anyMatch(e -> e.getExerciseType() == Exercise.ExerciseType.STRENGTH));
    }
}
