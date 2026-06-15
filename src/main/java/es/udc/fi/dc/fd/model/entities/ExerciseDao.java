package es.udc.fi.dc.fd.model.entities;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseDao extends JpaRepository<Exercise, Long> {

    boolean existsByNameIgnoreCase(String name);
    
    List<Exercise> findByEstado(Exercise.ExerciseEstado estado);

    Exercise findByName(String nombre);
    
    List<Exercise> findByEstadoAndBlockedFalse(Exercise.ExerciseEstado estado);
}