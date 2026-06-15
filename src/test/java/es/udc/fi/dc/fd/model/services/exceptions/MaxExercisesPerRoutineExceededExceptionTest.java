package es.udc.fi.dc.fd.model.services.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.Test;

public class MaxExercisesPerRoutineExceededExceptionTest {
    @Test
    public void testCreateDefault() {
        MaxExercisesPerRoutineExceededException e = new MaxExercisesPerRoutineExceededException();
        assertNotNull(e.getMessage());
    }
    @Test
    public void testCreateMessage() {
        MaxExercisesPerRoutineExceededException e = new MaxExercisesPerRoutineExceededException("Msg");
        assertEquals("Msg", e.getMessage());
    }
}