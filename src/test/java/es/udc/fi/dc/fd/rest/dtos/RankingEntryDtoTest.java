package es.udc.fi.dc.fd.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class RankingEntryDtoTest {

    @Test
    void testConstructorAndGetters() {
        RankingEntryDto dto = new RankingEntryDto(1L, "TestUser", BigDecimal.valueOf(100.5));

        assertEquals(1L, dto.getUserId());
        assertEquals("TestUser", dto.getDisplayName());
        assertEquals(BigDecimal.valueOf(100.5), dto.getValue());
    }

    @Test
    void testSetters() {
        RankingEntryDto dto = new RankingEntryDto(1L, "User", BigDecimal.ZERO);

        dto.setUserId(10L);
        dto.setDisplayName("NewUser");
        dto.setValue(BigDecimal.valueOf(500));

        assertEquals(10L, dto.getUserId());
        assertEquals("NewUser", dto.getDisplayName());
        assertEquals(BigDecimal.valueOf(500), dto.getValue());
    }
}
