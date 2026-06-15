package es.udc.fi.dc.fd.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.entities.Exercise;
import es.udc.fi.dc.fd.model.entities.Exercise.ExerciseEstado;
import static es.udc.fi.dc.fd.model.entities.Exercise.ExerciseEstado.APPROVED;

import es.udc.fi.dc.fd.model.entities.ExerciseDao;

// Servicio de ejercicios: listar por estado, aprobar, crear y eliminar
@Service
@Transactional
public class ExerciseServiceImpl implements ExerciseService {

    @Autowired
    private ExerciseDao exerciseDao;

    @Override
    // Lista ejercicios aprobados y no bloqueados (visibles para los usuarios)
    @Transactional(readOnly = true)
    public List<Exercise> listAll() {
        return exerciseDao.findByEstadoAndBlockedFalse(ExerciseEstado.APPROVED);
    }

    @Override
    // Lista TODOS los ejercicios aprobados (incluyendo bloqueados) - solo para
    // admin
    @Transactional(readOnly = true)
    public List<Exercise> listAllIncludingBlocked() {
        return exerciseDao.findByEstado(ExerciseEstado.APPROVED);
    }

    @Override
    // Lista ejercicios pendientes de aprobación
    @Transactional(readOnly = true)
    public List<Exercise> listPending() {
        return exerciseDao.findByEstado(ExerciseEstado.PENDING);
    }

    @Override
    // Marca un ejercicio como aprobado
    public void accept(Long exerciseId) {
        Exercise exercise = exerciseDao.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
        exercise.setestado(APPROVED);
        exerciseDao.save(exercise);
    }

    @Override
    // Elimina un ejercicio por id
    public void remove(Long exerciseId) {
        exerciseDao.deleteById(exerciseId);
    }

    @Override
    // Bloquea un ejercicio (solo admin; impide su uso en rutinas)
    public void block(Long exerciseId) {
        Exercise exercise = exerciseDao.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
        exercise.setBlocked(true);
        exerciseDao.save(exercise);
    }

    @Override
    // Crea un ejercicio (si no existe otro con el mismo nombre); queda en estado
    // PENDING
    @Transactional(noRollbackFor = DuplicateInstanceException.class)
    public Exercise createExercise(Exercise exercise) throws DuplicateInstanceException {
        if (exerciseDao.existsByNameIgnoreCase(exercise.getName())) {
            throw new DuplicateInstanceException("project.entities.exercise", exercise.getName());
        }
        
        exercise.setestado(ExerciseEstado.PENDING);

        return exerciseDao.save(exercise);
    }
}