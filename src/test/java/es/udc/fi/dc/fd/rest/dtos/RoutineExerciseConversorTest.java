package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;
import es.udc.fi.dc.fd.model.entities.*;

public class RoutineExerciseConversorTest {
    @Test
    public void testConversion() {
        Routine r = new Routine(); r.setId(1L);
        Exercise e = new Exercise(); e.setId(2L);
        RoutineExercise re = new RoutineExercise(r, e, 3, 12);
        re.setId(10L);
        
        RoutineExerciseDto dto = RoutineExerciseConversor.toRoutineExerciseDto(re);
        assertEquals(10L, dto.getId());
        assertEquals(1L, dto.getRoutineId());
        assertEquals(12, dto.getRepetitions());
        
        RoutineExercise back = RoutineExerciseConversor.toRoutineExercise(dto, r, e);
        assertEquals(10L, back.getId());
        assertEquals(3, back.getSets());
    }
}