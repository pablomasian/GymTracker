package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDateTime;
import org.junit.Test;

public class FollowerNotificationDtoTest {
    @Test
    public void testDto() {
        FollowerNotificationDto dto = new FollowerNotificationDto();
        dto.setId(1L);
        dto.setCoachId(2L);
        dto.setFollowerUsername("user");
        dto.setFollowerFirstName("First");
        dto.setFollowerLastName("Last");
        dto.setCreatedAt(LocalDateTime.MAX);
        dto.setRead(true);
        
        assertEquals(1L, dto.getId());
        assertEquals("user", dto.getFollowerUsername());
        assertEquals("First", dto.getFollowerFirstName());
        assertEquals(true, dto.isRead());
    }
}