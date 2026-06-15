package es.udc.fi.dc.fd.model.services.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.Test;

public class ExerciseProposalNotAllowedExceptionTest {
    @Test
    public void testCreateDefault() {
        ExerciseProposalNotAllowedException e = new ExerciseProposalNotAllowedException();
        assertNotNull(e.getMessage());
    }

    @Test
    public void testCreateMessage() {
        ExerciseProposalNotAllowedException e = new ExerciseProposalNotAllowedException("Error");
        assertEquals("Error", e.getMessage());
    }
}