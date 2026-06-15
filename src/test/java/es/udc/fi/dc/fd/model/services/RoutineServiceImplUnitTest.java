package es.udc.fi.dc.fd.model.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.RoutineDao;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.services.exceptions.MaxRoutinesExceededException;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;

@ExtendWith(MockitoExtension.class)
class RoutineServiceImplUnitTest {

    @Mock
    private RoutineDao routineDao;

    @Mock
    private UserDao userDao;

    @Mock
    private ExerciseDao exerciseDao;

    @Mock
    private RoutineExerciseDao routineExerciseDao;

    @Mock
    private RoutineExerciseService routineExerciseService;

    @InjectMocks
    private RoutineServiceImpl routineService;

    @Test
    void createRoutine_nonPremiumCoach_exceedsLimit_throws() {
        User coach = new User();
        coach.setId(100L);
        coach.setRole(User.RoleType.COACH);
        coach.setPremium(false);

        // Simulate 3 existing routines
        when(routineDao.findByUser(coach)).thenReturn(List.of(new Routine("a", coach), new Routine("b", coach), new Routine("c", coach)));

        assertThrows(MaxRoutinesExceededException.class, () -> routineService.createRoutine("nueva", coach));
    }

    @Test
    void publishRoutine_nonOwner_throwsPermission() {
        User owner = new User();
        owner.setId(1L);
        owner.setRole(User.RoleType.COACH);

        Routine r = new Routine("R", owner);
        r.setId(50L);

        User caller = new User();
        caller.setId(2L);
        caller.setRole(User.RoleType.USER);

        when(routineDao.findById(50L)).thenReturn(Optional.of(r));
        when(userDao.findById(2L)).thenReturn(Optional.of(caller));

        assertThrows(PermissionException.class, () -> routineService.publishRoutine(50L, 2L));
    }
}
