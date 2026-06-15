package es.udc.fi.dc.fd.rest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.ExerciseDao;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.RoutineDao;
import es.udc.fi.dc.fd.model.entities.RoutineExercise;
import es.udc.fi.dc.fd.model.entities.RoutineExerciseDao;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.User.RoleType;
import es.udc.fi.dc.fd.model.services.NotificationsFollowedCoachService;
import es.udc.fi.dc.fd.model.services.RoutineExerciseService;
import es.udc.fi.dc.fd.model.services.RoutineService;
import es.udc.fi.dc.fd.model.services.UserService;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;
import es.udc.fi.dc.fd.model.services.exceptions.MaxRoutinesExceededException;
import es.udc.fi.dc.fd.model.services.exceptions.MaxExercisesPerRoutineExceededException;
import es.udc.fi.dc.fd.model.services.exceptions.RoutineProposalNotAllowedException;
import es.udc.fi.dc.fd.rest.dtos.CreateRoutineExerciseRequest;
import es.udc.fi.dc.fd.rest.dtos.CreateRoutineRequest;
import es.udc.fi.dc.fd.rest.dtos.RoutineDetailConversor;
import es.udc.fi.dc.fd.rest.dtos.RoutineDetailDto;
import es.udc.fi.dc.fd.rest.dtos.RoutineDto;

@RestController
@RequestMapping("/api/routines")
public class RoutinesController {
    // Controlador REST para rutinas: listar, buscar, detalle y gestión por coaches

    private static final Logger log = LoggerFactory.getLogger(RoutinesController.class);

    @Autowired
    private RoutineService routineService;
    @Autowired
    private UserService userService;
    @Autowired
    private ExerciseDao exerciseDao;
    @Autowired
    private RoutineExerciseDao routineExerciseDao;
    @Autowired
    private RoutineExerciseService routineExerciseService;
    @Autowired
    private RoutineDao routineDao;
    @Autowired
    private NotificationsFollowedCoachService notificationsFollowedCoachService;

    // Lista todas las rutinas públicas
    @GetMapping("/display_all")
    public List<RoutineDto> displayAllRoutines() {
        List<Routine> routines = routineService.findPublic();
        return routines.stream()
                .map(r -> {
                    int count = routineExerciseDao.countByRoutine(r);
                    return new RoutineDto(
                            r.getId(),
                            r.getName(),
                            r.getUser() != null ? r.getUser().getId() : null,
                            r.getUser() != null ? r.getUser().getNombreUsuario() : null,
                            count,
                            r.isVisible());
                })
                .toList();
    }

    // Lista rutinas públicas creadas por un coach concreto
    @GetMapping("/display_by_coach")
    public List<RoutineDto> display_by_coach(@RequestParam Long coach_id) {
        List<Routine> routines = routineService.findRoutinesByCoach(coach_id);
        return routines.stream()
                .map(r -> {
                    int count = routineExerciseDao.countByRoutine(r);
                    return new RoutineDto(
                            r.getId(),
                            r.getName(),
                            r.getUser() != null ? r.getUser().getId() : null,
                            r.getUser() != null ? r.getUser().getNombreUsuario() : null,
                            count);
                })
                .toList();
    }

    // Búsqueda de rutinas públicas filtrando por material y músculos
    @GetMapping
    public List<RoutineDto> search(
            @RequestParam(required = false) List<String> equipment,
            @RequestParam(required = false) List<String> muscles,
            @RequestParam(required = false) String muscle) {
        List<Routine> routines = routineService.findPublic();

        if (equipment != null && !equipment.isEmpty()) {
            List<String> finalEquipment = equipment;
            routines = routines.stream()
                    .filter(r -> {
                        List<RoutineExercise> exercises = routineExerciseDao.findByRoutine(r);
                        return exercises.stream().anyMatch(
                                re -> finalEquipment.stream().anyMatch(eq -> re.getExercise().getEquipment() != null &&
                                        re.getExercise().getEquipment().toLowerCase().contains(eq.toLowerCase())));
                    })
                    .toList();
        }

        if ((muscles != null && !muscles.isEmpty()) || (muscle != null && !muscle.isBlank())) {
            List<String> muscleList = new java.util.ArrayList<>();
            if (muscles != null)
                muscleList.addAll(muscles);
            if (muscle != null && !muscle.isBlank())
                muscleList.add(muscle.trim());

            List<Routine> muscleFiltered = routineService.findByMuscles(muscleList);

            routines = routines.stream()
                    .filter(muscleFiltered::contains)
                    .toList();
        }

        return routines.stream()
                .map(r -> new RoutineDto(
                        r.getId(),
                        r.getName(),
                        r.getUser() != null ? r.getUser().getId() : null,
                        r.getUser() != null ? r.getUser().getNombreUsuario() : null,
                        routineExerciseDao.countByRoutine(r),
                        r.isVisible()))
                .toList();
    }

    // Obtiene una rutina por id (si es privada solo el propietario la ve)
    @GetMapping("/{id}")
    public RoutineDto get(@PathVariable Long id, @RequestAttribute(required = false) Long userId)
            throws InstanceNotFoundException {
        Routine r = routineService.findById(id);
        if (!r.isVisible() || r.getEstado() != Routine.RoutineEstado.APPROVED) {
            if (userId == null || (r.getUser() != null && !r.getUser().getId().equals(userId))) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Routine not found");
            }
        }
        int count = routineExerciseDao.countByRoutine(r);
        return new RoutineDto(r.getId(), r.getName(), r.getUser().getId(), r.getUser().getNombreUsuario(), count,
                r.isVisible());
    }

    // Devuelve el detalle de la rutina (con ejercicios)
    @GetMapping("/{id}/detail")
    public RoutineDetailDto getDetail(@PathVariable Long id, @RequestAttribute(required = false) Long userId) {
        Routine routine = routineDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Routine not found"));
        if (!routine.isVisible()) {
            if (userId == null || (routine.getUser() != null && !routine.getUser().getId().equals(userId))) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Routine not found");
            }
        }
        List<RoutineExercise> res = routineExerciseService.findExercisesByRoutine(routine);
        return RoutineDetailConversor.toRoutineDetailDto(routine, res);
    }

    // Lista los ejercicios de una rutina
    @GetMapping("/{id}/exercises")
    public List<Map<String, Object>> getExercises(@PathVariable Long id) throws InstanceNotFoundException {
        Routine r = routineService.findById(id);
        List<RoutineExercise> list = routineExerciseDao.findByRoutine(r);
        log.debug("[API] routine {} exercises fetched: {}", id, list.size());
        return list.stream().map(re -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", re.getId());
            m.put("exerciseId", re.getExercise().getId());
            m.put("name", re.getExercise().getName());
            m.put("description", re.getExercise().getDescription());
            m.put("muscles", re.getExercise().getMuscles());
            m.put("equipment", re.getExercise().getEquipment());
            // incluir URL de imagen si existe
            m.put("imageUrl", re.getExercise().getImageUrl());
            m.put("sets", re.getSets());
            m.put("repetitions", re.getRepetitions());
            m.put("weight", re.getWeight());
            m.put("blocked", re.getExercise().isBlocked());
            // Campos para cardio
            m.put("exerciseType", re.getExercise().getExerciseType() != null ? re.getExercise().getExerciseType().name()
                    : "STRENGTH");
            m.put("targetDistance", re.getTargetDistance());
            m.put("targetDuration", re.getTargetDuration());
            return m;
        }).toList();
    }

    // Crea una rutina (solo COACH/ADMIN) y añade ejercicios si se envían
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public RoutineDto create(@RequestAttribute Long userId,
            @Validated @RequestBody CreateRoutineRequest request)
            throws InstanceNotFoundException, MaxRoutinesExceededException, MaxExercisesPerRoutineExceededException {
        User coach = userService.loginFromId(userId);
        if (coach.getRole() != RoleType.COACH && coach.getRole() != RoleType.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only coaches can create routines");
        }

        // Validar límite de ejercicios para coaches no premium
        if (coach.getRole() == RoleType.COACH && (coach.getPremium() == null || !coach.getPremium())) {
            if (request.getExercises() != null && request.getExercises().size() > 5) {
                throw new MaxExercisesPerRoutineExceededException();
            }
        }

        Routine routine = routineService.createRoutine(coach.getId(), request.getName());
        log.debug("[API] creating routine id={} name='{}' requestedExercises={}", routine.getId(), request.getName(),
                request.getExercises() == null ? 0 : request.getExercises().size());

        int count = 0;
        int requested = request.getExercises() != null ? request.getExercises().size() : 0;
        if (request.getExercises() != null) {
            for (CreateRoutineExerciseRequest exReq : request.getExercises()) {
                if (exReq == null || exReq.getExerciseId() == null)
                    continue;
                Exercise ex = exerciseDao.findById(exReq.getExerciseId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Exercise not found: " + exReq.getExerciseId()));
                try {
                    log.debug("[API] adding exercise {} sets={} reps={} weight={} to routine {}", ex.getId(),
                            exReq.getSets(), exReq.getRepetitions(), exReq.getWeight(), routine.getId());
                    routineExerciseService.addRoutineExercise(coach, routine, ex, exReq.getSets(),
                            exReq.getRepetitions(), exReq.getWeight());
                    count++;
                } catch (es.udc.fi.dc.fd.model.services.exceptions.PermissionException e) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner of routine");
                }
            }
        }
        if (requested > 0 && count == 0) {
            log.warn("Routine {} created with 0 exercises but client requested {}. Likely invalid exercise IDs.",
                    routine.getId(), requested);
        }

        notificationsFollowedCoachService.notifyFollowers(coach.getId(), routine.getName());

        return new RoutineDto(routine.getId(), routine.getName(), coach.getId(), coach.getNombreUsuario(), count,
                routine.isVisible());
    }

    // Devuelve las rutinas del coach autenticado
    @GetMapping("/my-routines")
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public List<RoutineDto> getMyRoutines(@RequestAttribute Long userId) throws InstanceNotFoundException {
        List<Routine> routines = routineService.findRoutinesByCoach(userId);
        return routines.stream().map(r -> {
            int count = routineExerciseDao.countByRoutine(r);
            return new RoutineDto(r.getId(), r.getName(), r.getUser().getId(), r.getUser().getNombreUsuario(), count,
                    r.isVisible());
        }).toList();
    }

    // Actualiza una rutina propia (nombre y ejercicios)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public RoutineDto updateRoutine(@PathVariable Long id, @RequestAttribute Long userId,
            @Validated @RequestBody CreateRoutineRequest request)
            throws InstanceNotFoundException, PermissionException, MaxExercisesPerRoutineExceededException {

        Routine updatedRoutine = routineService.updateRoutine(id, userId, request.getName(), request.getExercises());
        int count = request.getExercises() != null ? request.getExercises().size() : 0;

        return new RoutineDto(updatedRoutine.getId(), updatedRoutine.getName(), userId,
                updatedRoutine.getUser().getNombreUsuario(), count, updatedRoutine.isVisible());
    }

    // Elimina una rutina propia
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public void deleteRoutine(@PathVariable Long id, @RequestAttribute Long userId)
            throws InstanceNotFoundException, PermissionException {
        routineService.deleteRoutine(id, userId);
    }

    // Publica una rutina para hacerla visible
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public RoutineDto publish(@PathVariable Long id, @RequestAttribute Long userId)
            throws InstanceNotFoundException, PermissionException, RoutineProposalNotAllowedException {
        Routine r = routineService.publishRoutine(id, userId);
        int count = routineExerciseDao.countByRoutine(r);
        return new RoutineDto(r.getId(), r.getName(), r.getUser() != null ? r.getUser().getId() : null,
                r.getUser() != null ? r.getUser().getNombreUsuario() : null, count, r.isVisible());
    }

    // Oculta una rutina publicada
    @PostMapping("/{id}/hide")
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public RoutineDto hide(@PathVariable Long id, @RequestAttribute Long userId)
            throws InstanceNotFoundException, PermissionException {
        Routine r = routineService.hideRoutine(id, userId);
        int count = routineExerciseDao.countByRoutine(r);
        return new RoutineDto(r.getId(), r.getName(), r.getUser() != null ? r.getUser().getId() : null,
                r.getUser() != null ? r.getUser().getNombreUsuario() : null, count, r.isVisible());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoutineDto> getPendingRoutines() {
        List<Routine> routines = routineService.findPending();
        return routines.stream().map(r -> new RoutineDto(r.getId(), r.getName(), r.getUser().getId(),
                r.getUser().getNombreUsuario(), routineExerciseDao.countByRoutine(r), r.isVisible())).toList();
    }

    @PutMapping("/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void approveRoutine(@PathVariable Long id) throws InstanceNotFoundException {
        routineService.approveRoutine(id);
    }

    @DeleteMapping("/{id}/dismiss")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void dismissRoutine(@PathVariable Long id) throws InstanceNotFoundException {
        routineService.dismissRoutine(id);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoutineDto> getAllRoutinesForAdmin() {
        List<Routine> routines = routineService.findAllRoutinesForAdmin();
        return routines.stream().map(r -> {
            int count = routineExerciseDao.countByRoutine(r);
            RoutineDto dto = new RoutineDto(r.getId(), r.getName(), r.getUser().getId(), r.getUser().getNombreUsuario(),
                    count, r.isVisible());
            return dto;
        }).toList();
    }

    @PutMapping("/{id}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void blockRoutine(@PathVariable Long id) throws InstanceNotFoundException {
        routineService.blockRoutine(id);
    }

    @PutMapping("/{id}/unblock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void unblockRoutine(@PathVariable Long id) throws InstanceNotFoundException {
        routineService.unblockRoutine(id);
    }
}
