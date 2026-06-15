package es.udc.fi.dc.fd.model.services.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;

public class BlockedUserExceptionTest {
    @Test
    public void testCreate() {
        String username = "pepe";
        BlockedUserException e = new BlockedUserException(username);
        assertEquals(username, e.getUsername());
        assertEquals("User 'pepe' has been blocked by administrator", e.getMessage());
    }
}