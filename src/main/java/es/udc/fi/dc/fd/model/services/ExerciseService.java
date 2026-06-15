package es.udc.fi.dc.fd.model.services;

import java.util.List;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.entities.Exercise;

public interface ExerciseService {
    List<Exercise> listAll();
    List<Exercise> listAllIncludingBlocked();
    List<Exercise> listPending();

    void remove(Long exerciseId);

    void accept (Long exerciseId);

    void block(Long exerciseId);

    Exercise createExercise(Exercise exercise) throws DuplicateInstanceException;
}
