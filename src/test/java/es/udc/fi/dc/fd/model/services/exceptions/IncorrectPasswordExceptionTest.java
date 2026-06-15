package es.udc.fi.dc.fd.model.services.exceptions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.Test;

public class IncorrectPasswordExceptionTest {
    @Test
    public void testCreate() {
        IncorrectPasswordException e = new IncorrectPasswordException();
        assertNotNull(e);
    }
}