package es.udc.fi.dc.fd.rest.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

public class JwtGeneratorImplTest {

    @Test
    public void generateAndParseToken() throws Exception {
        JwtGeneratorImpl gen = new JwtGeneratorImpl();

        // set private fields via reflection (use a 64+ bytes key for HS512)
        Field signKeyF = JwtGeneratorImpl.class.getDeclaredField("signKey");
        signKeyF.setAccessible(true);
        signKeyF.set(gen, "0123456789012345678901234567890123456789012345678901234567890123");

        Field expF = JwtGeneratorImpl.class.getDeclaredField("expirationMinutes");
        expF.setAccessible(true);
        expF.setLong(gen, 60L);

        JwtInfo info = new JwtInfo(42L, "alice", "USER");

        String token = gen.generate(info);
        JwtInfo parsed = gen.getInfo(token);

        assertEquals(info.getUserId(), parsed.getUserId());
        assertEquals(info.getUsername(), parsed.getUsername());
        assertEquals(info.getRole(), parsed.getRole());
    }

}
