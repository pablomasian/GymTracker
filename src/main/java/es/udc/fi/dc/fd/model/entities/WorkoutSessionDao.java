package es.udc.fi.dc.fd.model.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;

@Repository
public interface WorkoutSessionDao extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findByUserOrderByFechaDesc(User user);

    Optional<WorkoutSession> findById(Long sessionId);

    List<WorkoutSession> findByRoutine_User_IdOrderByFechaDesc(Long coachId);

    List<WorkoutSession> findByUserIdAndFechaAfterOrderByFechaDesc(Long userId, LocalDateTime startDate);

    List<WorkoutSession> findByUserIdAndRoutineId(Long userId, Long routineId);

    List<WorkoutSession> findByRoutine(Routine routine);

    long countByUser(User user);

}
