package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;

import es.udc.fi.dc.fd.model.entities.Like;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.WorkoutSession;

public class LikeConversorTest {

    @Test
    public void testConversion() throws Exception {
        User liker = new User(); 
        liker.setId(1L); 
        liker.setNombreUsuario("Liker");

        User liked = new User(); 
        liked.setId(2L); 
        liked.setNombreUsuario("Liked");

        Routine r = new Routine("R");
        
        WorkoutSession s = new WorkoutSession(liked, r, null);
        s.setId(5L);
        
        Like like = new Like(liker, liked, s);
        
        Field idField = Like.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(like, 99L);
        
        LikeDto dto = LikeConversor.toLikeDto(like);
        
        assertEquals(99L, dto.getId());
        assertEquals(1L, dto.getLiker_id());
        assertEquals(5L, dto.getSessionId());
        
        assertEquals(1, LikeConversor.toLikeDtos(List.of(like)).size());
    }
}