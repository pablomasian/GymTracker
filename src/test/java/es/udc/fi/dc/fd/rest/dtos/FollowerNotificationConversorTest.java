package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.Test;
import es.udc.fi.dc.fd.model.entities.FollowerNotification;
import es.udc.fi.dc.fd.model.entities.User;

public class FollowerNotificationConversorTest {

    @Test
    public void testConversion() {
        User coach = new User(); coach.setId(1L);
        User follower = new User(); follower.setId(2L); follower.setUsername("fan");
        
        FollowerNotification entity = new FollowerNotification(coach, follower);
        entity.setId(10L);
        entity.setCreatedAt(LocalDateTime.now());
        
        FollowerNotificationDto dto = FollowerNotificationConversor.toFollowerNotificationDto(entity);
        
        assertEquals(entity.getId(), dto.getId());
        assertEquals(follower.getUsername(), dto.getFollowerUsername());
        
        List<FollowerNotificationDto> list = FollowerNotificationConversor.toFollowerNotificationDtos(List.of(entity));
        assertEquals(1, list.size());
    }
}