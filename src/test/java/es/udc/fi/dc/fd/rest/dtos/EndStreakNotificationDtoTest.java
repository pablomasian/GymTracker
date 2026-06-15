package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class EndStreakNotificationDtoTest {

    @Test
    void testConstructorAndGetters() {
        LocalDateTime fechaLimite = LocalDateTime.now().plusHours(4);
        LocalDateTime fechaCreacion = LocalDateTime.now();

        EndStreakNotificationDto dto = new EndStreakNotificationDto(
                1L, 7, "¡Cuidado! Tu racha de 7 días terminará pronto",
                fechaLimite, fechaCreacion, false);

        assertEquals(1L, dto.getId());
        assertEquals(7, dto.getDiasRacha());
        assertEquals("¡Cuidado! Tu racha de 7 días terminará pronto", dto.getMensaje());
        assertEquals(fechaLimite, dto.getFechaLimite());
        assertEquals(fechaCreacion, dto.getFechaCreacion());
        assertFalse(dto.isLeido());
    }

    @Test
    void testConstructorWithReadTrue() {
        LocalDateTime now = LocalDateTime.now();

        EndStreakNotificationDto dto = new EndStreakNotificationDto(
                2L, 14, "Racha leída", now, now, true);

        assertEquals(2L, dto.getId());
        assertEquals(14, dto.getDiasRacha());
        assertTrue(dto.isLeido());
    }
}
