package es.udc.fi.dc.fd.model.entities;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.entities.Badge.BadgeType;

@Repository
public interface BadgeDao extends JpaRepository <Badge, Long> {
    
    boolean existsByUserIdAndType(long userId, BadgeType type);
    List<Badge> findByUserId(long userId);
   
}
