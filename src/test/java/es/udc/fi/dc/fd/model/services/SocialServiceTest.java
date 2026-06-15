package es.udc.fi.dc.fd.model.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import es.udc.fi.dc.fd.model.entities.*;

@RunWith(MockitoJUnitRunner.class)
public class SocialServiceTest {

    @Mock private UserDao userDao;
    @Mock private WorkoutSessionDao sessionDao;
    @Mock private LikeDao likeDao;
    @Mock private CommentDao commentDao;

    @InjectMocks
    private SocialServiceImpl socialService;

    @Test
    public void testLikeAndUnlike() {
        User u = new User(); u.setId(1L);
        WorkoutSession s = new WorkoutSession(); s.setId(2L); s.setUser(new User()); // Owner needed
        
        when(userDao.findById(1L)).thenReturn(Optional.of(u));
        when(sessionDao.findById(2L)).thenReturn(Optional.of(s));

        socialService.like(1L, 2L);
        verify(likeDao).save(any(Like.class));
        
        Like likeObj = new Like();
        when(likeDao.findByLikerIdAndSessionId(1L, 2L)).thenReturn(Optional.of(likeObj));
        socialService.unlike(1L, 2L);
        verify(likeDao).delete(likeObj);
    }
    
    @Test
    public void testComment() {
        User u = new User(); u.setId(1L);
        WorkoutSession s = new WorkoutSession(); s.setId(2L); s.setUser(new User());
        
        when(userDao.findById(1L)).thenReturn(Optional.of(u));
        when(sessionDao.findById(2L)).thenReturn(Optional.of(s));
        
        socialService.comment(1L, 2L, "Great job");
        verify(commentDao).save(any(Comment.class));
    }
    
    @Test
    public void testIsLiked() {
        WorkoutSession s = new WorkoutSession(); s.setId(5L);
        when(likeDao.existsByLikerIdAndSessionId(1L, 5L)).thenReturn(true);
        assertTrue(socialService.is_liked(1L, s));
    }
}