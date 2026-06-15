package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.User.RoleType;

public class UserConversorTest {

    @Test
    void testToUserDto() {
        User user = new User("display", "password", "John", "Doe", "johndoe");
        user.setId(1L);
        user.setEmail("john@test.com");
        user.setAvatarUrl("/avatar.png");
        user.setPremium(true);
        user.setBlocked(false);
        user.setRole(RoleType.USER);

        UserDto dto = UserConversor.toUserDto(user);

        assertEquals(1L, dto.getId());
        assertEquals("display", dto.getNombreUsuario());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("johndoe", dto.getUsername());
        assertEquals("USER", dto.getRole());
        assertEquals("john@test.com", dto.getEmail());
        assertEquals("/avatar.png", dto.getAvatarUrl());
        assertEquals(true, dto.getPremium());
        assertEquals(false, dto.getBlocked());
    }

    @Test
    void testToUser() {
        UserDto dto = new UserDto();
        dto.setNombreUsuario("display_name");
        dto.setPassword("secret");
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setUsername("janesmith");
        dto.setEmail("jane@test.com");
        dto.setAvatarUrl("/avatar2.png");
        dto.setPremium(true);
        dto.setRole("COACH");

        User user = UserConversor.toUser(dto);

        assertNotNull(user);
        assertEquals("display_name", user.getNombreUsuario());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("janesmith", user.getUsername());
        assertEquals("jane@test.com", user.getEmail());
        assertEquals("/avatar2.png", user.getAvatarUrl());
        assertEquals(true, user.getPremium());
        assertEquals(RoleType.COACH, user.getRole());
    }

    @Test
    void testToUserWithNullRole() {
        UserDto dto = new UserDto();
        dto.setNombreUsuario("user1");
        dto.setPassword("pass");
        dto.setFirstName("First");
        dto.setLastName("Last");
        dto.setUsername("login");
        dto.setRole(null);

        User user = UserConversor.toUser(dto);

        assertNotNull(user);
        // Role should remain as default (USER)
    }

    @Test
    void testToUserWithInvalidRole() {
        UserDto dto = new UserDto();
        dto.setNombreUsuario("user2");
        dto.setPassword("pass");
        dto.setFirstName("First");
        dto.setLastName("Last");
        dto.setUsername("login2");
        dto.setRole("INVALID_ROLE");

        User user = UserConversor.toUser(dto);

        assertNotNull(user);
        // Role should remain unchanged due to exception handling
    }

    @Test
    void testToUserPrivateDto() {
        User user = new User("display", "password", "John", "Doe", "johndoe");
        user.setId(1L);
        user.setEmail("john@test.com");
        user.setAvatarUrl("/avatar.png");
        user.setWeight(75.0);
        user.setHeight(180.0);
        user.setAge(25);
        user.setGender("MALE");

        UserPrivateDto dto = UserConversor.toUserPrivateDto(user);

        assertEquals(1L, dto.getId());
        assertEquals("display", dto.getNombreUsuario());
        assertEquals("johndoe", dto.getUsername());
        assertEquals("john@test.com", dto.getEmail());
        assertEquals("/avatar.png", dto.getAvatarUrl());
        assertEquals(75.0, dto.getWeight());
        assertEquals(180.0, dto.getHeight());
        assertEquals(25, dto.getAge());
        assertEquals("MALE", dto.getGender());
    }

    @Test
    void testToUserPublicDto() {
        User user = new User("display", "password", "John", "Doe", "johndoe");
        user.setId(1L);
        user.setAvatarUrl("/avatar.png");

        UserDto dto = UserConversor.toUserPublicDto(user);

        assertEquals(1L, dto.getId());
        assertEquals("display", dto.getNombreUsuario());
        assertEquals("johndoe", dto.getUsername());
        assertEquals("/avatar.png", dto.getAvatarUrl());
    }

    @Test
    void testToAuthenticatedUserDto() {
        User user = new User("display", "password", "John", "Doe", "johndoe");
        user.setId(1L);
        user.setRole(RoleType.USER);

        AuthenticatedUserDto dto = UserConversor.toAuthenticatedUserDto("token123", user);

        assertNotNull(dto);
        assertEquals("token123", dto.getServiceToken());
        assertNotNull(dto.getUserDto());
        assertEquals(1L, dto.getUserDto().getId());
    }
}
