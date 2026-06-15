package es.udc.fi.dc.fd.model.services.exceptions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.Test;

public class PermissionExceptionTest {
    @Test
    public void testCreate() {
        PermissionException e = new PermissionException();
        assertNotNull(e);
    }
}