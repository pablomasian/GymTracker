package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Test;

import es.udc.fi.dc.fd.model.entities.NotificationFollowedCoach;
import es.udc.fi.dc.fd.model.entities.User;

public class NotificationFollowedCoachConversorTest {

    @Test
    public void testToAndFromDto() {
        User recipient = new User();
        recipient.setId(20L);
        recipient.setUsername("recipient");

        User coach = new User();
        coach.setId(30L);
        coach.setUsername("coachUser");

        NotificationFollowedCoach n = new NotificationFollowedCoach("RoutineX", recipient, coach);
        n.setId(77L);
        LocalDateTime now = LocalDateTime.of(2022,1,2,3,4);
        n.setCreatedAt(now);

        NotificationFollowedCoachDto dto = NotificationFollowedCoachConversor.toNotificationFollowedCoachDto(n);

        assertEquals(n.getId(), dto.getId());
        assertEquals(n.getRoutineName(), dto.getRoutineName());
        assertEquals(recipient.getId(), dto.getUserId());
        assertEquals(coach.getId(), dto.getCoachId());
        assertEquals(coach.getUsername(), dto.getCoachName());
        assertEquals(n.isRead(), dto.isRead());
        assertEquals(now, dto.getCreatedAt());

        // reverse conversion
        NotificationFollowedCoach fromDto = NotificationFollowedCoachConversor.toNotificationFollowedCoach(dto, recipient, coach);
        assertEquals(dto.getRoutineName(), fromDto.getRoutineName());
    }
}
