package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import org.junit.Test;

public class RoutineDetailDtoTest {
    @Test
    public void testDto() {
        RoutineDetailDto dto = new RoutineDetailDto();
        dto.setId(1L);
        dto.setName("Name");
        dto.setUserId(2L);
        dto.setExercises(new ArrayList<>());
        
        assertEquals(1L, dto.getId());
        assertEquals("Name", dto.getName());
        assertEquals(2L, dto.getUserId());
        assertEquals(0, dto.getExercises().size());
    }
}