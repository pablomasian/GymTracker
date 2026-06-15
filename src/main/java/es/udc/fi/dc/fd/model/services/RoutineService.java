package es.udc.fi.dc.fd.model.services;

import java.util.List;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;
import es.udc.fi.dc.fd.model.services.exceptions.MaxRoutinesExceededException;
import es.udc.fi.dc.fd.model.services.exceptions.MaxExercisesPerRoutineExceededException;
import es.udc.fi.dc.fd.rest.dtos.CreateRoutineExerciseRequest;

public interface RoutineService {
    // Creation (coach provided by caller)
    Routine createRoutine(String name, User coach) throws MaxRoutinesExceededException;

    // Legacy helper (used by existing tests): create by userId
    Routine createRoutine(Long userId, String name) throws InstanceNotFoundException, MaxRoutinesExceededException;

    // Basic queries
    Routine findById(Long id) throws InstanceNotFoundException;
    List<Routine> findAll();

    // Search filters (optional fragments)
    List<Routine> search(String nameFragment, String coachNombreUsuarioFragment);

    // Legacy style helpers for existing controller code
    default List<Routine> displayAllRoutines() { return findAll(); }
    // Real implementation should throw if user no existe
    List<Routine> findRoutinesByUser(Long userId) throws InstanceNotFoundException;

    List<Routine> findRoutinesByCoach(Long coachId);

    Routine updateRoutine(Long routineId, Long coachId, String name, List<CreateRoutineExerciseRequest> exercises)
            throws InstanceNotFoundException, PermissionException, MaxExercisesPerRoutineExceededException;

    void deleteRoutine(Long routineId, Long coachId) throws InstanceNotFoundException, PermissionException;

    List<Routine> findByMuscle(String muscle);
    
    List<Routine> findByMuscles(List<String> muscles);

    // Visibility
    List<Routine> findPublic();
    Routine publishRoutine(Long routineId, Long coachId) throws InstanceNotFoundException, PermissionException, es.udc.fi.dc.fd.model.services.exceptions.RoutineProposalNotAllowedException;
    Routine hideRoutine(Long routineId, Long coachId) throws InstanceNotFoundException, PermissionException;

    List<Routine> findPending();
    void approveRoutine(Long routineId) throws InstanceNotFoundException;
    void dismissRoutine(Long routineId) throws InstanceNotFoundException;

    List<Routine> findAllRoutinesForAdmin();
    void blockRoutine(Long routineId) throws InstanceNotFoundException;
    void unblockRoutine(Long routineId) throws InstanceNotFoundException;

}
