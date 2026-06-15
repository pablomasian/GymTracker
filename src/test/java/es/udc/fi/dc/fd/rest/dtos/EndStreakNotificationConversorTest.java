package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.udc.fi.dc.fd.model.entities.NotificationEndStreak;

public class EndStreakNotificationConversorTest {

    @Test
    void testToDto() {
        NotificationEndStreak notification = mock(NotificationEndStreak.class);
        LocalDateTime fechaLimite = LocalDateTime.now().plusHours(4);
        LocalDateTime fechaCreacion = LocalDateTime.now();

        when(notification.getId()).thenReturn(1L);
        when(notification.getDiasRacha()).thenReturn(7);
        when(notification.getMensaje()).thenReturn("Tu racha terminará pronto");
        when(notification.getFechaLimite()).thenReturn(fechaLimite);
        when(notification.getFechaCreacion()).thenReturn(fechaCreacion);
        when(notification.isLeido()).thenReturn(false);

        EndStreakNotificationDto dto = EndStreakNotificationConversor.toDto(notification);

        assertEquals(1L, dto.getId());
        assertEquals(7, dto.getDiasRacha());
        assertEquals("Tu racha terminará pronto", dto.getMensaje());
        assertEquals(fechaLimite, dto.getFechaLimite());
        assertEquals(fechaCreacion, dto.getFechaCreacion());
        assertFalse(dto.isLeido());
    }

    @Test
    void testToDtos() {
        NotificationEndStreak n1 = mock(NotificationEndStreak.class);
        when(n1.getId()).thenReturn(1L);
        when(n1.getDiasRacha()).thenReturn(5);
        when(n1.getMensaje()).thenReturn("Mensaje 1");
        when(n1.getFechaLimite()).thenReturn(LocalDateTime.now());
        when(n1.getFechaCreacion()).thenReturn(LocalDateTime.now());
        when(n1.isLeido()).thenReturn(false);

        NotificationEndStreak n2 = mock(NotificationEndStreak.class);
        when(n2.getId()).thenReturn(2L);
        when(n2.getDiasRacha()).thenReturn(10);
        when(n2.getMensaje()).thenReturn("Mensaje 2");
        when(n2.getFechaLimite()).thenReturn(LocalDateTime.now());
        when(n2.getFechaCreacion()).thenReturn(LocalDateTime.now());
        when(n2.isLeido()).thenReturn(true);

        List<EndStreakNotificationDto> dtos = EndStreakNotificationConversor.toDtos(Arrays.asList(n1, n2));

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals(2L, dtos.get(1).getId());
        assertFalse(dtos.get(0).isLeido());
        assertTrue(dtos.get(1).isLeido());
    }
}
