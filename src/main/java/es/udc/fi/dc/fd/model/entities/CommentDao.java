package es.udc.fi.dc.fd.model.entities;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentDao extends JpaRepository <Comment, Long> {
    
    List<Comment> findAllByCommentedId(Long userId);

    List<Comment> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
}

