package es.udc.fi.dc.fd.model.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BlockDao extends JpaRepository<Block, Long> {
    
    Optional<Block> findByBlockerAndBlocked(User blocker, User blocked);
    
    boolean existsByBlockerAndBlocked(User blocker, User blocked);
    
    @Query("SELECT b.blocked.id FROM Block b WHERE b.blocker.id = :blockerId")
    List<Long> findBlockedUserIdsByBlockerId(@Param("blockerId") Long blockerId);
    
    @Query("SELECT b.blocker.id FROM Block b WHERE b.blocked.id = :blockedId")
    List<Long> findBlockerUserIdsByBlockedId(@Param("blockedId") Long blockedId);
}
