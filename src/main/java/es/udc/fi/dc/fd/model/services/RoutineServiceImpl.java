package es.udc.fi.dc.fd.model.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.Routine.RoutineEstado;
import es.udc.fi.dc.fd.model.entities.RoutineDao;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.SetLogDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.User.RoleType;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;
import es.udc.fi.dc.fd.model.services.exceptions.MaxRoutinesExceededException;
import es.udc.fi.dc.fd.model.services.exceptions.MaxExercisesPerRoutineExceededException;
import es.udc.fi.dc.fd.rest.dtos.CreateRoutineExerciseRequest;

@Service
@Transactional
public class RoutineServiceImpl implements RoutineService {

    @Autowired
    private RoutineDao routineDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ExerciseDao exerciseDao;

    @Autowired
    private RoutineExerciseDao routineExerciseDao;

    @Autowired
    private WorkoutSessionDao workoutSessionDao;

    @Autowired
    private SetLogDao setLogDao;

    @Autowired
    private RoutineExerciseService routineExerciseService;

    @Autowired(required = false)
    private PermissionChecker permissionChecker;

    @Override
    public Routine createRoutine(String name, User coach) throws MaxRoutinesExceededException {
        // Validar límites de coaches no premium
        if (coach.getRole() == RoleType.COACH && (coach.getPremium() == null || !coach.getPremium())) {
            long routineCount = routineDao.findByUser(coach).size();
            if (routineCount >= 3) {
                throw new MaxRoutinesExceededException();
            }
        }

        Routine r = new Routine(name, coach);
        r.setEstado(RoutineEstado.APPROVED);
        return routineDao.save(r);
    }

    @Override
    public Routine createRoutine(Long userId, String name)
            throws InstanceNotFoundException, MaxRoutinesExceededException {
        User coach;
        if (permissionChecker != null) {
            coach = permissionChecker.checkUser(userId);
        } else {
            coach = userDao.findById(userId)
                    .orElseThrow(() -> new InstanceNotFoundException("project.entities.user", userId));
        }
        return createRoutine(name, coach);
    }

    @Override
    @Transactional(readOnly = true)
    public Routine findById(Long id) throws InstanceNotFoundException {
        Optional<Routine> opt = routineDao.findById(id);
        if (opt.isEmpty()) {
            throw new InstanceNotFoundException("project.entities.routine", id);
        }
        return opt.get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Routine> findAll() {
        return routineDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Routine> findRoutinesByUser(Long userId) throws InstanceNotFoundException {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.user", userId));
        return routineDao.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Routine> search(String nameFragment, String coachNombreUsuarioFragment) {
        String nf = nameFragment != null ? nameFragment.trim().toLowerCase() : null;
        String cf = coachNombreUsuarioFragment != null ? coachNombreUsuarioFragment.trim().toLowerCase() : null;
        if (nf == null && cf == null) {
            return findPublic();
        }
        return routineDao.findAll().stream().filter(r -> {
            if (!r.isVisible() || r.getEstado() != RoutineEstado.APPROVED)
                return false;
            boolean ok = true;
            if (nf != null && !nf.isEmpty()) {
                ok &= r.getName() != null && r.getName().toLowerCase().contains(nf);
            }
            if (ok && cf != null && !cf.isEmpty()) {
                ok &= r.getUser() != null && r.getUser().getNombreUsuario() != null
                        && r.getUser().getNombreUsuario().toLowerCase().contains(cf);
            }
            return ok;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Routine> findRoutinesByCoach(Long coachId) {
        return routineDao.findByUser_Id(coachId);
    }

    @Override
    public Routine updateRoutine(Long routineId, Long coachId, String name,
            List<CreateRoutineExerciseRequest> exercises)
            throws InstanceNotFoundException, PermissionException, MaxExercisesPerRoutineExceededException {
        Routine routine = findById(routineId);
        User coach = userDao.findById(coachId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.user", coachId));
        if (!routine.getUser().getId().equals(coachId) && !coach.getRole().equals(RoleType.ADMIN)) {
            throw new PermissionException();
        }

        // Validar límite de ejercicios para coaches no premium
        if (coach.getRole() == RoleType.COACH && (coach.getPremium() == null || !coach.getPremium())) {
            if (exercises != null && exercises.size() > 5) {
                throw new MaxExercisesPerRoutineExceededException();
            }
        }

        routine.setName(name);
        routine.setEstado(RoutineEstado.APPROVED);

        routineExerciseDao.deleteAll(routineExerciseDao.findByRoutine(routine));

        if (exercises != null) {
            for (CreateRoutineExerciseRequest exReq : exercises) {
                Exercise ex = exerciseDao.findById(exReq.getExerciseId())
                        .orElseThrow(() -> new InstanceNotFoundException("project.entities.exercise",
                                exReq.getExerciseId()));
                routineExerciseService.addRoutineExercise(coach, routine, ex, exReq.getSets(), exReq.getRepetitions(),
                        exReq.getWeight());
            }
        }
        return routine;
    }

    @Override
    public void deleteRoutine(Long routineId, Long coachId) throws InstanceNotFoundException, PermissionException {
        Routine routine = findById(routineId);
        boolean isAdmin = userDao.findById(coachId)
                .map(u -> u.getRole().equals(RoleType.ADMIN))
                .orElse(false);

        if (!routine.getUser().getId().equals(coachId) && !isAdmin) {
            throw new PermissionException();
        }

        // Delete all associated routine exercises first to avoid FK constraint
        // violation
        routineExerciseDao.deleteAll(routineExerciseDao.findByRoutine(routine));

        // Delete all associated set logs for each workout session to avoid FK
        // constraint violation
        List<WorkoutSession> sessions = workoutSessionDao.findByRoutine(routine);
        for (WorkoutSession session : sessions) {
            setLogDao.deleteAll(setLogDao.findBySession(session));
        }

        // Delete all associated workout sessions to avoid FK constraint violation
        workoutSessionDao.deleteAll(sessions);

        routineDao.delete(routine);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Routine> findByMuscle(String muscle) {
        String m = (muscle == null) ? "" : muscle.trim();
        if (m.isEmpty()) {
            return findPublic();
        }
        return routineExerciseDao.findRoutinesByMuscle(m).stream()
                .filter(r -> r.isVisible() && r.getEstado() == RoutineEstado.APPROVED)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Routine> findByMuscles(List<String> muscles) {
        if (muscles == null || muscles.isEmpty()) {
            return findPublic();
        }
        return muscles.stream()
                .flatMap(m -> routineExerciseDao.findRoutinesByMuscle(m.trim()).stream())
                .filter(r -> r.isVisible() && r.getEstado() == RoutineEstado.APPROVED)
                .distinct()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Routine> findPublic() {
        return routineDao.findByEstadoAndVisibleTrue(RoutineEstado.APPROVED).stream()
                .filter(r -> !r.isBlocked())
                .collect(Collectors.toList());
    }

    @Override
    public Routine publishRoutine(Long routineId, Long coachId) throws InstanceNotFoundException, PermissionException,
            es.udc.fi.dc.fd.model.services.exceptions.RoutineProposalNotAllowedException {
        Routine routine = findById(routineId);
        User coach = userDao.findById(coachId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.user", coachId));
        boolean isAdmin = RoleType.ADMIN.equals(coach.getRole());
        if (!routine.getUser().getId().equals(coachId) && !isAdmin) {
            throw new PermissionException();
        }
        // If owner is a non-premium coach, they are not allowed to propose/publish
        // routines
        if (!isAdmin && coach.getRole() == RoleType.COACH && (coach.getPremium() == null || !coach.getPremium())) {
            throw new es.udc.fi.dc.fd.model.services.exceptions.RoutineProposalNotAllowedException();
        }
        routine.setVisible(true);
        return routine;
    }

    @Override
    public Routine hideRoutine(Long routineId, Long coachId) throws InstanceNotFoundException, PermissionException {
        Routine routine = findById(routineId);
        User coach = userDao.findById(coachId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.user", coachId));
        boolean isAdmin = RoleType.ADMIN.equals(coach.getRole());
        if (!routine.getUser().getId().equals(coachId) && !isAdmin) {
            throw new PermissionException();
        }
        routine.setVisible(false);
        return routine;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Routine> findPending() {
        return routineDao.findByEstado(RoutineEstado.APPROVED);
    }

    @Override
    public void approveRoutine(Long routineId) throws InstanceNotFoundException {
        Routine routine = findById(routineId);
        routine.setEstado(RoutineEstado.APPROVED);
    }

    @Override
    public void dismissRoutine(Long routineId) throws InstanceNotFoundException {
        if (!routineDao.existsById(routineId)) {
            throw new InstanceNotFoundException("project.entities.routine", routineId);
        }
        routineDao.deleteById(routineId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Routine> findAllRoutinesForAdmin() {
        return routineDao.findAll();
    }

    @Override
    public void blockRoutine(Long routineId) throws InstanceNotFoundException {
        Routine routine = findById(routineId);
        routine.setBlocked(true);
        routineDao.save(routine);
    }

    @Override
    public void unblockRoutine(Long routineId) throws InstanceNotFoundException {
        Routine routine = findById(routineId);
        routine.setBlocked(false);
        routineDao.save(routine);
    }
}