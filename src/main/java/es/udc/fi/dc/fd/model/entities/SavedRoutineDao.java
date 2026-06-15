package es.udc.fi.dc.fd.model.entities;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedRoutineDao extends JpaRepository<SavedRoutine, Long> {
    Optional<SavedRoutine> findByUserAndRoutine(User user, Routine routine);
    List<SavedRoutine> findByUserOrderByCreatedAtDesc(User user);
}
