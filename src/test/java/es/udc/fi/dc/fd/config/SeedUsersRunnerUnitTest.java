package es.udc.fi.dc.fd.config;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.UserDao;

import java.lang.reflect.Field;

class SeedUsersRunnerUnitTest {

    @Mock
    private UserDao userDao;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private SeedUsersRunner runner;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        runner = new SeedUsersRunner();
        Field f1 = SeedUsersRunner.class.getDeclaredField("userDao");
        f1.setAccessible(true);
        f1.set(runner, userDao);
        Field f2 = SeedUsersRunner.class.getDeclaredField("passwordEncoder");
        f2.setAccessible(true);
        f2.set(runner, passwordEncoder);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) mocks.close();
    }

    @Test
    void run_whenUsersNotPresent_createsUsers() throws Exception {
        when(userDao.findByUsername("coach")).thenReturn(java.util.Optional.empty());
        when(userDao.findByUsername("user")).thenReturn(java.util.Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        runner.run(new String[0]);

        verify(userDao, atLeastOnce()).save(any(User.class));
    }

}
