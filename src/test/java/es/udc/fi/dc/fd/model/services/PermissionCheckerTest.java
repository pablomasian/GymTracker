package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PermissionCheckerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionChecker permissionChecker;

    private User testUser;

    @Before
    public void setUp() throws DuplicateInstanceException {
        testUser = new User("testuser", "password", "Test", "User", "testuser");
        userService.signUp(testUser);
    }

    @Test
    public void testCheckUser_Success() throws InstanceNotFoundException {
        User foundUser = permissionChecker.checkUser(testUser.getId());
        assertNotNull(foundUser);
        assertEquals(testUser.getId(), foundUser.getId());
    }

    @Test
    public void testCheckUser_Failure() {
        assertThrows(InstanceNotFoundException.class, () -> {
            permissionChecker.checkUser(-1L);
        });
    }

    @Test
    public void testCheckUserExists_Success() throws InstanceNotFoundException {
        permissionChecker.checkUserExists(testUser.getId());
    }

    @Test
    public void testCheckUserExists_Failure() {
        assertThrows(InstanceNotFoundException.class, () -> {
            permissionChecker.checkUserExists(-1L);
        });
    }
}