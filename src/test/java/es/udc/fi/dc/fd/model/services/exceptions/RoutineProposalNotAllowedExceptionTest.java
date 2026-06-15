package es.udc.fi.dc.fd.model.services.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.Test;

public class RoutineProposalNotAllowedExceptionTest {
    @Test
    public void testCreateDefault() {
        RoutineProposalNotAllowedException e = new RoutineProposalNotAllowedException();
        assertNotNull(e.getMessage());
    }
    @Test
    public void testCreateMessage() {
        RoutineProposalNotAllowedException e = new RoutineProposalNotAllowedException("Msg");
        assertEquals("Msg", e.getMessage());
    }
}