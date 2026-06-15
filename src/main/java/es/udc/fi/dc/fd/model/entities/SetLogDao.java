package es.udc.fi.dc.fd.model.entities;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;

@Repository
public interface SetLogDao extends JpaRepository<SetLog, Long> {
       List<SetLog> findBySessionId(Long sessionId);

       List<SetLog> findBySession(WorkoutSession session);

       @Query("SELECT sl FROM SetLog sl " +
                     "JOIN sl.session s " +
                     "WHERE s.user.id = :userId " +
                     "AND sl.exercise.id = :exerciseId " +
                     "ORDER BY s.fecha ASC")
       List<SetLog> findByUserIdAndExerciseIdOrderByDate(@Param("userId") Long userId,
                     @Param("exerciseId") Long exerciseId);
}
