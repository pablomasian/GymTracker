package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.udc.fi.dc.fd.model.entities.Badge;
import es.udc.fi.dc.fd.model.entities.Badge.BadgeType;
import es.udc.fi.dc.fd.model.entities.User;

public class BadgeConversorTest {

    @Test
    void testToBadgeDto() {
        User user = new User("test", "password", "Test", "User", "testuser");
        Badge badge = new Badge(user, LocalDateTime.now(), BadgeType.HUNDRED);
        badge.setId(1L);

        BadgeDto dto = BadgeConversor.toBadgeDto(badge);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(user, dto.getUser());
        assertEquals("HUNDRED", dto.getType());
    }

    @Test
    void testToBadgeDtos() {
        User user = new User("test", "password", "Test", "User", "testuser");
        Badge badge1 = new Badge(user, LocalDateTime.now(), BadgeType.HUNDRED);
        badge1.setId(1L);
        Badge badge2 = new Badge(user, LocalDateTime.now(), BadgeType.FIFTY_WORKOUTS);
        badge2.setId(2L);

        List<BadgeDto> dtos = BadgeConversor.toBadgeDtos(Arrays.asList(badge1, badge2));

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("HUNDRED", dtos.get(0).getType());
        assertEquals("FIFTY_WORKOUTS", dtos.get(1).getType());
    }
}
