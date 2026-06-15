package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.List;
import org.junit.Test;
import es.udc.fi.dc.fd.model.entities.SetLog;

public class SetLogConversorTest {
    @Test
    public void testToSetLogDto() {
        SetLog log = new SetLog();
        log.setId(1L);
        
        SetLogDto dto = SetLogConversor.toSetLogDto(log);
        assertEquals(1L, dto.getId());
        assertNull(dto.getExerciseName());
        
        assertNull(SetLogConversor.toSetLogDto(null));
        
        assertEquals(1, SetLogConversor.toSetLogDtos(List.of(log)).size());
    }
}