package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UserDtoTest {

    @Test
    void testDefaultConstructor() {
        UserDto dto = new UserDto();
        assertNull(dto.getId());
        assertNull(dto.getUsername());
        assertNull(dto.getNombreUsuario());
        assertNull(dto.getPassword());
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNull(dto.getEmail());
        assertNull(dto.getAvatarUrl());
        assertNull(dto.getPremium());
        assertNull(dto.getRole());
        assertNull(dto.getBlocked());
    }

    @Test
    void testParameterizedConstructor() {
        UserDto dto = new UserDto(1L, "display_name", "John", "Doe", "johndoe", "USER");

        assertEquals(1L, dto.getId());
        assertEquals("display_name", dto.getNombreUsuario());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("johndoe", dto.getUsername());
        assertEquals("USER", dto.getRole());
    }

    @Test
    void testParameterizedConstructorWithNulls() {
        UserDto dto = new UserDto(1L, null, null, null, null, "USER");

        assertNull(dto.getNombreUsuario());
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNull(dto.getUsername());
    }

    @Test
    void testSetters() {
        UserDto dto = new UserDto();

        dto.setId(10L);
        dto.setNombreUsuario("  my_user  ");
        dto.setUsername("  mylogin  ");
        dto.setPassword("secret123");
        dto.setFirstName("  Jane  ");
        dto.setLastName("  Smith  ");
        dto.setEmail("  jane@test.com  ");
        dto.setAvatarUrl("  /avatar.png  ");
        dto.setPremium(true);
        dto.setRole("COACH");
        dto.setBlocked(false);

        assertEquals(10L, dto.getId());
        assertEquals("my_user", dto.getNombreUsuario());
        assertEquals("mylogin", dto.getUsername());
        assertEquals("secret123", dto.getPassword());
        assertEquals("Jane", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertEquals("jane@test.com", dto.getEmail());
        assertEquals("/avatar.png", dto.getAvatarUrl());
        assertTrue(dto.getPremium());
        assertEquals("COACH", dto.getRole());
        assertEquals(false, dto.getBlocked());
    }

    @Test
    void testSettersWithNullsAndBlanks() {
        UserDto dto = new UserDto();

        dto.setNombreUsuario(null);
        dto.setUsername(null);
        dto.setEmail(null);
        dto.setEmail("  "); // blank
        dto.setAvatarUrl(null);
        dto.setAvatarUrl("  "); // blank

        assertNull(dto.getNombreUsuario());
        assertNull(dto.getUsername());
        assertNull(dto.getEmail());
        assertNull(dto.getAvatarUrl());
    }
}
