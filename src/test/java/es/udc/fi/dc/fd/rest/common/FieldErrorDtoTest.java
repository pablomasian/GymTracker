package es.udc.fi.dc.fd.rest.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;

public class FieldErrorDtoTest {
    @Test
    public void testDto() {
        FieldErrorDto dto = new FieldErrorDto("field", "msg");
        assertEquals("field", dto.getFieldName());
        assertEquals("msg", dto.getMessage());
        
        dto.setFieldName("f2");
        dto.setMessage("m2");
        assertEquals("f2", dto.getFieldName());
        assertEquals("m2", dto.getMessage());
    }
}