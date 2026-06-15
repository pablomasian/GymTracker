package es.udc.fi.dc.fd.rest.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;

public class JwtInfoTest {
    @Test
    public void testInfo() {
        JwtInfo info = new JwtInfo(1L, "user", "ROLE");
        assertEquals(1L, info.getUserId());
        assertEquals("user", info.getUsername());
        assertEquals("ROLE", info.getRole());

        info.setUserId(2L);
        info.setUsername("user2");
        info.setRole("ADMIN");
        assertEquals(2L, info.getUserId());
        assertEquals("user2", info.getUsername());
        assertEquals("ADMIN", info.getRole());
    }
}