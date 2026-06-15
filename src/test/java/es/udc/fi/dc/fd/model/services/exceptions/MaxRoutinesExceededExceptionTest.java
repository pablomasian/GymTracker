package es.udc.fi.dc.fd.model.services.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.Test;

public class MaxRoutinesExceededExceptionTest {
    @Test
    public void testCreateDefault() {
        MaxRoutinesExceededException e = new MaxRoutinesExceededException();
        assertNotNull(e.getMessage());
    }
    @Test
    public void testCreateMessage() {
        MaxRoutinesExceededException e = new MaxRoutinesExceededException("Msg");
        assertEquals("Msg", e.getMessage());
    }
}