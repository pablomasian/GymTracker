package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;
import es.udc.fi.dc.fd.model.entities.*;

public class RoutineDetailConversorTest {
    @Test
    public void testConversion() {
        User u = new User(); u.setId(1L);
        Routine r = new Routine("R", u); r.setId(1L);
        Exercise ex = new Exercise("E", "D", "M"); ex.setId(2L);
        RoutineExercise re = new RoutineExercise(r, ex, 3, 10);
        re.setWeight(BigDecimal.TEN);
        
        RoutineDetailDto dto = RoutineDetailConversor.toRoutineDetailDto(r, List.of(re));
        
        assertEquals(1L, dto.getId());
        assertEquals("R", dto.getName());
        assertEquals(1, dto.getExercises().size());
        assertEquals("E", dto.getExercises().get(0).getExerciseName());
    }
}