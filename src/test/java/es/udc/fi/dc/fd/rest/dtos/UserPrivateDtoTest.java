package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;

public class UserPrivateDtoTest {
    @Test
    public void testDto() {
        UserPrivateDto dto = new UserPrivateDto();
        dto.setId(1L);
        dto.setWeight(80.0);
        dto.setHeight(180.0);
        dto.setAge(20);
        dto.setGender("M");
        dto.setBmi(25.0);
        
        assertEquals(1L, dto.getId());
        assertEquals(80.0, dto.getWeight());
        assertEquals(180.0, dto.getHeight());
        assertEquals(20, dto.getAge());
        assertEquals("M", dto.getGender());
        assertEquals(25.0, dto.getBmi());
    }
}