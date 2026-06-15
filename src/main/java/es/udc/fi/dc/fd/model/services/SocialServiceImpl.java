package es.udc.fi.dc.fd.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.udc.fi.dc.fd.model.entities.Comment;
import es.udc.fi.dc.fd.model.entities.CommentDao;
import es.udc.fi.dc.fd.model.entities.Like;
import es.udc.fi.dc.fd.model.entities.LikeDao;
import es.udc.fi.dc.fd.model.entities.NotificationFollowedCoach;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;
import es.udc.fi.dc.fd.model.entities.WorkoutSessionDao;

@Service
public class SocialServiceImpl implements SocialService {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private LikeDao likeDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private WorkoutSessionDao workoutSessionDao;

    @Override
    public void like (Long userId, Long sessionId){

        User user = userDao.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        WorkoutSession session = workoutSessionDao.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        
        User liked = workoutSessionDao.findById(sessionId).get().getUser();

        Like like = new Like (user, liked,session);

        likeDao.save(like);
    }

    @Override
    public void unlike (Long userId, Long sessionId){
        
        Like like = likeDao.findByLikerIdAndSessionId(userId, sessionId).orElseThrow(() -> new RuntimeException("Like not found"));

        likeDao.delete(like);
    
    }

    @Override
    public boolean is_liked (Long userId, WorkoutSession session){
        
        Long sessionId = session.getId();

        return likeDao.existsByLikerIdAndSessionId(userId, sessionId);
    }

    @Override
    public List<Like> getMyLikes (Long userId){

        return likeDao.findAllByLikedId(userId);
    }


    @Override
    public void comment (Long userId, Long sessionId, String text){


        User user = userDao.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        WorkoutSession session = workoutSessionDao.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));

        User commented = workoutSessionDao.findById(sessionId).get().getUser();

        Comment comment = new Comment (session, user, commented, text);

        commentDao.save(comment);
    }


    @Override
    public List<Comment> getMyComments (Long commentedId){

        return commentDao.findAllByCommentedId(commentedId);
    }

    @Override
    public void markLikeAsRead(Long likeId) {
        Like like = likeDao.findById(likeId)
            .orElseThrow(() -> new IllegalArgumentException("Like no encontrado"));
        like.setRead(true);
        likeDao.save(like);
    }

    @Override
    public void markCommentAsRead(Long commentId) {
        Comment comment = commentDao.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment no encontrado"));
        comment.setRead(true);
        commentDao.save(comment);
    }

    @Override
    public int getSessionLikesCount (Long sessionId){
        long count = likeDao.countBySessionId(sessionId);

        return (int) count;
    }

    @Override
    public List<User> getSessionLikers(Long sessionId){
        return likeDao.findLikersBySessionId(sessionId);
    }

    @Override
    public List<Comment> getSessionComments(Long sessionId){
        return commentDao.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }



}
