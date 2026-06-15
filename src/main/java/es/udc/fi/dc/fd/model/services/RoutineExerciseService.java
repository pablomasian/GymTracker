package es.udc.fi.dc.fd.model.services;

import java.util.List;

import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineExercise;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;

public interface RoutineExerciseService {

    RoutineExercise addRoutineExercise(User user, Routine routine, Exercise exercise, int sets, int repetitions, java.math.BigDecimal weight)
            throws PermissionException;

    List<RoutineExercise> findExercisesByRoutine(Routine routine);

}
