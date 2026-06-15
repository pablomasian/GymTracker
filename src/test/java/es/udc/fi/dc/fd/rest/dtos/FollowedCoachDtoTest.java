package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class FollowedCoachDtoTest {

    @Test
    void testConstructorAndGetters() {
        FollowedCoachDto dto = new FollowedCoachDto(1L, "user1", "User One", "http://avatar.url", "COACH");

        assertEquals(1L, dto.getId());
        assertEquals("user1", dto.getUsername());
        assertEquals("User One", dto.getNombreUsuario());
        assertEquals("http://avatar.url", dto.getAvatarUrl());
        assertEquals("COACH", dto.getRole());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        FollowedCoachDto dto = new FollowedCoachDto();
        assertNull(dto.getId());

        dto.setId(2L);
        dto.setUsername("user2");
        dto.setNombreUsuario("User Two");
        dto.setAvatarUrl("http://avatar2.url");
        dto.setRole("ADMIN");

        assertEquals(2L, dto.getId());
        assertEquals("user2", dto.getUsername());
        assertEquals("User Two", dto.getNombreUsuario());
        assertEquals("http://avatar2.url", dto.getAvatarUrl());
        assertEquals("ADMIN", dto.getRole());
    }
}