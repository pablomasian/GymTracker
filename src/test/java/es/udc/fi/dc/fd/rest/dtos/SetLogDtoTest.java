package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class SetLogDtoTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        SetLogDto dto = new SetLogDto();
        dto.setId(1L);
        dto.setSessionId(2L);
        dto.setExerciseId(3L);
        dto.setExerciseName("Ex");
        dto.setImageUrl("img");
        dto.setNumeroSerie(1);
        dto.setRepeticiones(10);
        dto.setPeso(BigDecimal.ONE);

        // Cardio fields
        dto.setDistancia(new BigDecimal("5.5"));
        dto.setDuracion(30);
        dto.setExerciseType("CARDIO");

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getSessionId());
        assertEquals(3L, dto.getExerciseId());
        assertEquals("Ex", dto.getExerciseName());
        assertEquals("img", dto.getImageUrl());
        assertEquals(1, dto.getNumeroSerie());
        assertEquals(10, dto.getRepeticiones());
        assertEquals(BigDecimal.ONE, dto.getPeso());
        assertEquals(new BigDecimal("5.5"), dto.getDistancia());
        assertEquals(30, dto.getDuracion());
        assertEquals("CARDIO", dto.getExerciseType());
    }

    @Test
    void testStrengthConstructor() {
        SetLogDto dto = new SetLogDto(1L, 2L, 3L, "Push Up", "http://img.com", 1, 15, new BigDecimal("20.0"));

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getSessionId());
        assertEquals(3L, dto.getExerciseId());
        assertEquals("Push Up", dto.getExerciseName());
        assertEquals("http://img.com", dto.getImageUrl());
        assertEquals(1, dto.getNumeroSerie());
        assertEquals(15, dto.getRepeticiones());
        assertEquals(new BigDecimal("20.0"), dto.getPeso());
        assertEquals("STRENGTH", dto.getExerciseType()); // Default set in constructor
        assertNull(dto.getDistancia());
        assertNull(dto.getDuracion());
    }

    @Test
    void testCardioConstructor() {
        SetLogDto dto = new SetLogDto(1L, 2L, 3L, "Run", "http://run.com", 1, 0, BigDecimal.ZERO,
                new BigDecimal("5.0"), 30, "CARDIO");

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getSessionId());
        assertEquals(3L, dto.getExerciseId());
        assertEquals("Run", dto.getExerciseName());
        assertEquals("http://run.com", dto.getImageUrl());
        assertEquals(1, dto.getNumeroSerie());
        assertEquals(0, dto.getRepeticiones());
        assertEquals(BigDecimal.ZERO, dto.getPeso());
        assertEquals(new BigDecimal("5.0"), dto.getDistancia());
        assertEquals(30, dto.getDuracion());
        assertEquals("CARDIO", dto.getExerciseType());
    }
}