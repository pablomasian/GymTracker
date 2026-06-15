package es.udc.fi.dc.fd.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineExercise;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.User.RoleType;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;
import jakarta.transaction.Transactional;

// Servicio de ejercicios dentro de una rutina: alta y consulta
@Service
@Transactional
public class RoutineExerciseServiceImpl implements RoutineExerciseService {

    @Autowired
    RoutineExerciseDao routineExerciseDao;

    @Override
    // Añade un ejercicio a una rutina si el usuario es el propietario o ADMIN
    public RoutineExercise addRoutineExercise(User user, Routine routine, Exercise exercise, int sets, int repetitions, java.math.BigDecimal weight)
            throws PermissionException {

        // Compare by id to avoid failing when persistence context returns different
        // managed instances (reference inequality).
        if (routine.getUser() == null || user == null || (routine.getUser().getId() != user.getId()) && !user.getRole().equals(RoleType.ADMIN)) {
            throw new PermissionException();
        }

        RoutineExercise routineExercise = new RoutineExercise(routine, exercise, sets, repetitions);
        routineExercise.setWeight(weight);
        return routineExerciseDao.save(routineExercise);
    }

    @Override
    // Lista los ejercicios asociados a una rutina
    public List<RoutineExercise> findExercisesByRoutine(Routine routine) {
        return routineExerciseDao.findByRoutine(routine);
    }
}
