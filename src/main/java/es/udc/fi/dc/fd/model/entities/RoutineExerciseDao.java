package es.udc.fi.dc.fd.model.entities;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineExerciseDao extends JpaRepository<RoutineExercise, Long> {
    int countByRoutine(Routine routine);
    List<RoutineExercise> findByRoutine(Routine routine);

    @Query("""
        SELECT DISTINCT re.routine
        FROM RoutineExercise re
        WHERE LOWER(re.exercise.muscles) LIKE LOWER(CONCAT('%', :muscle, '%'))
    """)
    List<Routine> findRoutinesByMuscle(@Param("muscle") String muscle);
}
