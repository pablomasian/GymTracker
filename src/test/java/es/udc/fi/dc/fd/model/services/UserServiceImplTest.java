package es.udc.fi.dc.fd.model.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.services.exceptions.BlockedUserException;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectLoginException;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectPasswordException;

import java.lang.reflect.Field;

class UserServiceImplTest {

    private UserServiceImpl service;
    private PermissionChecker permissionChecker;
    private BCryptPasswordEncoder passwordEncoder;
    private UserDao userDao;
    private es.udc.fi.dc.fd.model.entities.BlockDao blockDao;

    @BeforeEach
    void setup() throws Exception {
        service = new UserServiceImpl();
        permissionChecker = mock(PermissionChecker.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        userDao = mock(UserDao.class);
        blockDao = mock(es.udc.fi.dc.fd.model.entities.BlockDao.class);

        // inject mocks
        setField(service, "permissionChecker", permissionChecker);
        setField(service, "passwordEncoder", passwordEncoder);
        setField(service, "userDao", userDao);
        setField(service, "blockDao", blockDao);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void signUp_success() throws Exception {
        User u = new User("nu", "pw", "F", "L", "uname");
        when(userDao.existsByUsername(u.getUsername())).thenReturn(false);
        when(passwordEncoder.encode("pw")).thenReturn("encoded");

        service.signUp(u);

        assertEquals("encoded", u.getPassword());
        assertTrue(u.getAvatarUrl().contains(u.getUsername()));
        verify(userDao).save(u);
    }

    @Test
    void signUp_duplicate() {
        User u = new User("nu", "pw", "F", "L", "uname");
        when(userDao.existsByUsername(u.getUsername())).thenReturn(true);

        assertThrows(DuplicateInstanceException.class, () -> service.signUp(u));
        verify(userDao, never()).save(any());
    }

    @Test
    void login_success_and_blocked() throws Exception {
        User u = new User("nu", "pw", "F", "L", "uname");
        u.setPassword("encoded");
        when(userDao.findByUsername("uname")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("pw", "encoded")).thenReturn(true);

        User res = service.login("uname", "pw");
        assertSame(u, res);

        // blocked case
        u.setBlocked(true);
        when(userDao.findByUsername("uname")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("pw", "encoded")).thenReturn(true);

        assertThrows(BlockedUserException.class, () -> service.login("uname", "pw"));
    }

    @Test
    void login_incorrect() {
        when(userDao.findByUsername("x")).thenReturn(Optional.empty());
        assertThrows(IncorrectLoginException.class, () -> service.login("x", "pw"));
    }

    @Test
    void changePassword_success_and_fail() throws Exception {
        User u = new User("nu", "oldEncoded", "F", "L", "uname");
        u.setPassword("oldEncoded");
        when(permissionChecker.checkUser(1L)).thenReturn(u);

        when(passwordEncoder.matches("old", "oldEncoded")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("newEncoded");

        service.changePassword(1L, "old", "new");
        assertEquals("newEncoded", u.getPassword());

        // wrong old password
        when(passwordEncoder.matches("bad", "oldEncoded")).thenReturn(false);
        assertThrows(IncorrectPasswordException.class, () -> service.changePassword(1L, "bad", "x"));
    }

    @Test
    void updateProfile_and_block_unblock_search() throws Exception {
        User u = new User("nu", "pw", "F", "L", "uname");
        u.setId(10L);
        u.setRole(User.RoleType.COACH);
        when(permissionChecker.checkUser(10L)).thenReturn(u);

        User updated = service.updateProfile(10L, "NewF", "NewL", "newu", "nuser", "email@e.com", 70.0, 175.0, 28, "F", true);
        assertEquals("NewF", updated.getFirstName());
        assertEquals("nuser", updated.getNombreUsuario());
        verify(userDao).save(u);

        // searchUsers filters requester and blocked users
        User a = new User(); a.setId(1L); a.setUsername("a"); a.setBlocked(false);
        User b = new User(); b.setId(2L); b.setUsername("b"); b.setBlocked(false);
        User c = new User(); c.setId(3L); c.setUsername("c"); c.setBlocked(false);

        when(userDao.findByUsernameContainingIgnoreCase("q")).thenReturn(Arrays.asList(a, b, c));
        when(blockDao.findBlockedUserIdsByBlockerId(1L)).thenReturn(Arrays.asList(2L));
        when(blockDao.findBlockerUserIdsByBlockedId(1L)).thenReturn(Arrays.asList());
        
        List<User> res = service.searchUsers(" q ", 1L);
        assertEquals(1, res.size()); // should exclude user 1 (requester) and user 2 (blocked by requester), leaving only user 3
        assertTrue(res.stream().noneMatch(user -> user.getId() == 1L || user.getId() == 2L));
        assertTrue(res.stream().anyMatch(user -> user.getId() == 3L));
    }

    @Test
    void getPublicUserProfile_notFound() {
        when(userDao.findById(999L)).thenReturn(Optional.empty());
        assertThrows(InstanceNotFoundException.class, () -> service.getPublicUserProfile(999L));
    }

}
