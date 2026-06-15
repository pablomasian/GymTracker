package es.udc.fi.dc.fd.rest.controllers;

import static es.udc.fi.dc.fd.rest.dtos.ExerciseConversor.toExercise;
import static es.udc.fi.dc.fd.rest.dtos.ExerciseConversor.toExerciseDto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.User.RoleType;
import es.udc.fi.dc.fd.model.services.ExerciseService;
import es.udc.fi.dc.fd.model.services.UserService;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;
import es.udc.fi.dc.fd.model.services.exceptions.ExerciseProposalNotAllowedException;
import es.udc.fi.dc.fd.rest.dtos.ExerciseDto;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    // Controlador REST de ejercicios: listado, revisión y creación

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<ExerciseDto> list() {
        return exerciseService.listAll().stream()
            .map(e -> toExerciseDto(e))
            .collect(Collectors.toList());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ExerciseDto> listAllForAdmin() {
        // Lista TODOS los ejercicios aprobados (incluyendo bloqueados) para admin
        return exerciseService.listAllIncludingBlocked().stream()
            .map(e -> toExerciseDto(e))
            .collect(Collectors.toList());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ExerciseDto> listPending() {
        return exerciseService.listPending().stream()
            .map(e -> toExerciseDto(e))
            .collect(Collectors.toList());
    }

    @PutMapping("/{exerciseId}/accept")
    @PreAuthorize("hasRole('ADMIN')")
    public void accept (@PathVariable Long exerciseId){
        exerciseService.accept(exerciseId);
    }

    @DeleteMapping("/{exerciseId}/dismiss")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete (@PathVariable Long exerciseId){
        exerciseService.remove(exerciseId);
    }

    @PutMapping("/{exerciseId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public void block(@PathVariable Long exerciseId) {
        exerciseService.block(exerciseId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public ExerciseDto createExercise(@RequestAttribute Long userId, @Validated @RequestBody ExerciseDto exerciseDto) 
            throws DuplicateInstanceException, InstanceNotFoundException, PermissionException, ExerciseProposalNotAllowedException {
        // Verificar si el usuario es un coach no premium (no pueden proponer ejercicios)
        User user = userService.loginFromId(userId);
        if (user.getRole() == RoleType.COACH && (user.getPremium() == null || !user.getPremium())) {
            throw new ExerciseProposalNotAllowedException();
        }
        
        Exercise exercise = toExercise(exerciseDto);
        Exercise createdExercise = exerciseService.createExercise(exercise);
        return toExerciseDto(createdExercise);
    }
}