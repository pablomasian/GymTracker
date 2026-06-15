package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ExerciseProgressDtoTest {

    @Test
    void testConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal maxWeight = new BigDecimal("100.5");
        int totalReps = 50;
        int totalSets = 5;

        ExerciseProgressDto dto = new ExerciseProgressDto(now, maxWeight, totalReps, totalSets);

        assertEquals(now, dto.getFecha());
        assertEquals(maxWeight, dto.getMaxWeight());
        assertEquals(totalReps, dto.getTotalReps());
        assertEquals(totalSets, dto.getTotalSets());
    }

    @Test
    void testSetters() {
        ExerciseProgressDto dto = new ExerciseProgressDto(null, null, 0, 0);

        LocalDateTime now = LocalDateTime.now();
        BigDecimal maxWeight = new BigDecimal("80.0");

        dto.setFecha(now);
        dto.setMaxWeight(maxWeight);
        dto.setTotalReps(30);
        dto.setTotalSets(3);

        assertEquals(now, dto.getFecha());
        assertEquals(maxWeight, dto.getMaxWeight());
        assertEquals(30, dto.getTotalReps());
        assertEquals(3, dto.getTotalSets());
    }
}
