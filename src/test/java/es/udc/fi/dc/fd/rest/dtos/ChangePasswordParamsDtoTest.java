package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;

public class ChangePasswordParamsDtoTest {
    @Test
    public void testDto() {
        ChangePasswordParamsDto dto = new ChangePasswordParamsDto();
        dto.setOldPassword("old");
        dto.setNewPassword("new");
        assertEquals("old", dto.getOldPassword());
        assertEquals("new", dto.getNewPassword());
    }
}