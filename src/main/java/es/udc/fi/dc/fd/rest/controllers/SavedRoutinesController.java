package es.udc.fi.dc.fd.rest.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.*;
import es.udc.fi.dc.fd.model.services.UserService;

@RestController
@RequestMapping("/api/saved-routines")
public class SavedRoutinesController {
    // Controlador REST para guardar y listar rutinas favoritas del usuario

    @Autowired private SavedRoutineDao savedRoutineDao;
    @Autowired private RoutineDao routineDao;
    @Autowired private UserService userService;
    @Autowired private NotificationDao notificationDao;

    // Guarda una rutina para el usuario (idempotente) y notifica al coach
    @PostMapping("/{routineId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> save(@RequestAttribute Long userId, @PathVariable Long routineId) throws InstanceNotFoundException {
        User user = userService.loginFromId(userId);
        Routine routine = routineDao.findById(routineId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Routine not found"));

        // idempotente
        if (savedRoutineDao.findByUserAndRoutine(user, routine).isEmpty()) {
            savedRoutineDao.save(new SavedRoutine(user, routine));
            // Crear notificación para el coach propietario de la rutina
            User coach = routine.getUser();
            if (coach != null && coach.getId() != null && !coach.getId().equals(user.getId())) {
                String message = String.format("%s ha guardado tu rutina '%s'", 
                    (user.getNombreUsuario() != null ? user.getNombreUsuario() : user.getUsername()),
                    routine.getName());
                notificationDao.save(new Notification(coach, "SAVED_ROUTINE", message));
            }
        }
        return Map.of("status", "ok");
    }

    // Elimina una rutina guardada
    @DeleteMapping("/{routineId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsave(@RequestAttribute Long userId, @PathVariable Long routineId) throws InstanceNotFoundException {
        User user = userService.loginFromId(userId);
        Routine routine = routineDao.findById(routineId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Routine not found"));
        savedRoutineDao.findByUserAndRoutine(user, routine).ifPresent(savedRoutineDao::delete);
    }

    // Lista las rutinas guardadas por el usuario
    @GetMapping
    public List<Map<String,Object>> mySaved(@RequestAttribute Long userId) throws InstanceNotFoundException {
        User user = userService.loginFromId(userId);
        return savedRoutineDao.findByUserOrderByCreatedAtDesc(user).stream()
            .map(sr -> {
                java.util.Map<String,Object> m = new java.util.HashMap<>();
                m.put("id", sr.getId());
                m.put("routineId", sr.getRoutine().getId());
                m.put("name", sr.getRoutine().getName());
                m.put("coachId", sr.getRoutine().getUser() != null ? sr.getRoutine().getUser().getId() : null);
                m.put("coachName", sr.getRoutine().getUser() != null ? sr.getRoutine().getUser().getNombreUsuario() : null);
                return m;
            })
            .collect(Collectors.toList());
    }
}
