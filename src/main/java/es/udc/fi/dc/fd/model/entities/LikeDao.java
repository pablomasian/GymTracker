package es.udc.fi.dc.fd.model.entities;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeDao extends JpaRepository <Like, Long> {
    
    Optional<Like> findByLikerIdAndSessionId(Long userId, Long sessionId);

    void deleteByLikerIdAndSessionId(Long userId, Long sessionId);

    boolean existsByLikerIdAndSessionId(Long userId, Long sessionId);

    List<Like> findAllByLikedId(Long likedId);

    List<Like> findAllByLikerId(Long likedId);

    Long countBySessionId(Long sessionId);

    @Query("SELECT l.liker FROM Like l WHERE l.session.id = :sessionId")
    List<User> findLikersBySessionId(@Param("sessionId") Long sessionId);

}
