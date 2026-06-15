package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;

public class LoginParamsDtoTest {
    @Test
    public void testDto() {
        LoginParamsDto dto = new LoginParamsDto();
        dto.setUsername(" user ");
        dto.setPassword("pass");
        assertEquals("user", dto.getUsername());
        assertEquals("pass", dto.getPassword());
    }
}