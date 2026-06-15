package es.udc.fi.dc.fd.model.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StreakCronTest {

    @Mock
    private StreakService streakService;

    private StreakCron streakCron;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        streakCron = new StreakCron(streakService);
    }

    @Test
    void testEjecutarAvisosFinDeRacha() {
        // When
        streakCron.ejecutarAvisosFinDeRacha();

        // Then
        verify(streakService, times(1)).generarAvisosFinDeRacha();
    }
}
