package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;

public class RoutineExerciseDtoTest {
    @Test
    public void testDto() {
        RoutineExerciseDto dto = new RoutineExerciseDto();
        dto.setId(1L);
        dto.setRoutineId(2L);
        dto.setExerciseId(3L);
        dto.setSets(4);
        dto.setRepetitions(12);
        
        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getRoutineId());
        assertEquals(3L, dto.getExerciseId());
        assertEquals(4, dto.getSets());
        assertEquals(12, dto.getRepetitions());
    }
}