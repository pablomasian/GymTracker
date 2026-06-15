package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class AuthenticatedUserDtoTest {

    @Test
    void testDefaultConstructor() {
        AuthenticatedUserDto dto = new AuthenticatedUserDto();
        assertNull(dto.getServiceToken());
        assertNull(dto.getUserDto());
    }

    @Test
    void testParameterizedConstructor() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");

        AuthenticatedUserDto dto = new AuthenticatedUserDto("token123", userDto);

        assertEquals("token123", dto.getServiceToken());
        assertNotNull(dto.getUserDto());
        assertEquals(1L, dto.getUserDto().getId());
        assertEquals("testuser", dto.getUserDto().getUsername());
    }

    @Test
    void testSetters() {
        AuthenticatedUserDto dto = new AuthenticatedUserDto();
        UserDto userDto = new UserDto();
        userDto.setId(5L);

        dto.setServiceToken("newToken");
        dto.setUserDto(userDto);

        assertEquals("newToken", dto.getServiceToken());
        assertEquals(5L, dto.getUserDto().getId());
    }
}
