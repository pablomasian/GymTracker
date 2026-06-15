package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.Test;
import es.udc.fi.dc.fd.model.entities.Routine;
import es.udc.fi.dc.fd.model.entities.User;

public class RoutineConversorTest {
    @Test
    public void testToDtoAndBack() {
        User u = new User(); u.setId(1L); u.setNombreUsuario("Coach");
        Routine r = new Routine("Legs", u);
        r.setId(10L);
        
        RoutineDto dto = RoutineConversor.toRoutineDto(r);
        assertEquals(10L, dto.getId());
        assertEquals("Legs", dto.getName());
        assertEquals("Coach", dto.getCoachNombreUsuario());
        
        Routine r2 = RoutineConversor.toRoutine(dto, u);
        assertEquals(10L, r2.getId());
        assertEquals("Legs", r2.getName());
        
        assertEquals(1, RoutineConversor.toRoutineDtos(List.of(r)).size());
    }
}