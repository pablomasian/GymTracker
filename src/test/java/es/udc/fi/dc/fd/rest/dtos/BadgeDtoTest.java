package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import es.udc.fi.dc.fd.model.entities.Badge.BadgeType;
import es.udc.fi.dc.fd.model.entities.User;

public class BadgeDtoTest {

    @Test
    void testDefaultConstructor() {
        BadgeDto dto = new BadgeDto();
        assertEquals(0, dto.getId());
        assertNull(dto.getUser());
        assertNull(dto.getDate());
        assertNull(dto.getIconUrl());
        assertNull(dto.getType());
        assertNull(dto.getDescription());
    }

    @Test
    void testParameterizedConstructor() {
        User user = new User("test", "password", "Test", "User", "testuser");
        LocalDateTime now = LocalDateTime.now();

        BadgeDto dto = new BadgeDto(1L, user, now, BadgeType.HUNDRED, "100kg badge", "/icons/badge.png");

        assertEquals(1L, dto.getId());
        assertEquals(user, dto.getUser());
        assertEquals(now, dto.getDate());
        assertEquals("HUNDRED", dto.getType());
        assertEquals("100kg badge", dto.getDescription());
        assertEquals("/icons/badge.png", dto.getIconUrl());
    }

    @Test
    void testSettersAndGetters() {
        BadgeDto dto = new BadgeDto();
        User user = new User("test2", "password", "Test2", "User2", "testuser2");
        LocalDateTime now = LocalDateTime.now();

        dto.setId(5L);
        dto.setUser(user);
        dto.setDate(now);
        dto.setIconUrl("/new/icon.png");
        dto.setType("STREAK_7");
        dto.setDescription("7 day streak");

        assertEquals(5L, dto.getId());
        assertEquals(user, dto.getUser());
        assertEquals(now, dto.getDate());
        assertEquals("/new/icon.png", dto.getIconUrl());
        assertEquals("STREAK_7", dto.getType());
        assertEquals("7 day streak", dto.getDescription());
    }
}
