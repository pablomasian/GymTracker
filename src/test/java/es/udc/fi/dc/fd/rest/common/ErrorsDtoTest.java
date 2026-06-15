package es.udc.fi.dc.fd.rest.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.List;
import org.junit.Test;

public class ErrorsDtoTest {
    @Test
    public void testGlobal() {
        ErrorsDto dto = new ErrorsDto("Global");
        assertEquals("Global", dto.getGlobalError());
    }
    @Test
    public void testFields() {
        List<FieldErrorDto> list = List.of(new FieldErrorDto("f","m"));
        ErrorsDto dto = new ErrorsDto(list);
        assertEquals(list, dto.getFieldErrors());
        assertNull(dto.getGlobalError());
    }
}