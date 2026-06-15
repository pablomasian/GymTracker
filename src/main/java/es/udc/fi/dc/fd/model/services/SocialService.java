package es.udc.fi.dc.fd.model.services;

import java.util.List;

import es.udc.fi.dc.fd.model.entities.Comment;
import es.udc.fi.dc.fd.model.entities.Like;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;

public interface SocialService {
    
    void like (Long userId, Long sessionId);

    void unlike (Long userId, Long sessionId);

    boolean is_liked (Long userId, WorkoutSession session);

    List<Like> getMyLikes (Long userId);

    void comment (Long userId, Long sessionId, String text);

    List<Comment> getMyComments (Long commentedId);

    public void markLikeAsRead(Long likeId);

    public void markCommentAsRead(Long commentId);

    int getSessionLikesCount (Long sessionId);

    List<User> getSessionLikers(Long sessionId);

    List<Comment> getSessionComments(Long sessionId);


}
