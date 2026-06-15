package es.udc.fi.dc.fd.model.services.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;

public class IncorrectLoginExceptionTest {
    @Test
    public void testCreate() {
        IncorrectLoginException e = new IncorrectLoginException("user", "pass");
        assertEquals("user", e.getUsername());
        assertEquals("pass", e.getPassword());
    }
}