package es.udc.fi.dc.fd.rest.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.services.exceptions.MaxExercisesPerRoutineExceededException;
import es.udc.fi.dc.fd.model.services.exceptions.MaxRoutinesExceededException;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;
import es.udc.fi.dc.fd.model.services.exceptions.RoutineProposalNotAllowedException;
import es.udc.fi.dc.fd.model.services.exceptions.ExerciseProposalNotAllowedException;

import static org.mockito.Mockito.*;

public class CommonControllerAdviceTest {

    private CommonControllerAdvice advice;
    private MessageSource messageSource;

    @BeforeEach
    public void setUp() {
        advice = new CommonControllerAdvice();
        messageSource = mock(MessageSource.class);
        // inject mock via reflection
        try {
            java.lang.reflect.Field f = CommonControllerAdvice.class.getDeclaredField("messageSource");
            f.setAccessible(true);
            f.set(advice, messageSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void handleInstanceNotFound() {
        InstanceNotFoundException ex = new InstanceNotFoundException("user", 123L);

        when(messageSource.getMessage(eq("user"), any(), anyString(), any(Locale.class))).thenReturn("usuario");
        when(messageSource.getMessage(eq("project.exceptions.InstanceNotFoundException"), any(Object[].class),
                anyString(), any(Locale.class)))
                .thenReturn("No encontrado");

        ErrorsDto dto = advice.handleInstanceNotFoundException(ex, Locale.getDefault());

        assertEquals("No encontrado", dto.getGlobalError());
    }

    @Test
    public void testHandleDuplicateInstanceException_WithDot() {
        DuplicateInstanceException ex = new DuplicateInstanceException("project.entities.exercise", "Push Up");

        ErrorsDto result = advice.handleDuplicateInstanceException(ex, Locale.ENGLISH);

        assertNotNull(result);
    }

    @Test
    public void testHandleDuplicateInstanceException_WithoutDot() {
        DuplicateInstanceException ex = new DuplicateInstanceException("user", "testuser");

        ErrorsDto result = advice.handleDuplicateInstanceException(ex, Locale.ENGLISH);

        assertNotNull(result);
    }

    @Test
    public void testHandleDuplicateInstanceException_NullName() {
        DuplicateInstanceException ex = new DuplicateInstanceException(null, "value");

        ErrorsDto result = advice.handleDuplicateInstanceException(ex, Locale.ENGLISH);

        assertNotNull(result);
    }

    @Test
    public void testHandlePermissionException() {
        PermissionException ex = new PermissionException();
        when(messageSource.getMessage(eq("project.exceptions.PermissionException"), any(), anyString(),
                any(Locale.class)))
                .thenReturn("Permission denied");

        ErrorsDto result = advice.handlePermissionException(ex, Locale.ENGLISH);

        assertNotNull(result);
    }

    @Test
    public void testHandleMaxRoutinesExceededException() {
        MaxRoutinesExceededException ex = new MaxRoutinesExceededException();

        ErrorsDto result = advice.handleMaxRoutinesExceededException(ex);

        assertNotNull(result);
    }

    @Test
    public void testHandleMaxExercisesPerRoutineExceededException() {
        MaxExercisesPerRoutineExceededException ex = new MaxExercisesPerRoutineExceededException();

        ErrorsDto result = advice.handleMaxExercisesPerRoutineExceededException(ex);

        assertNotNull(result);
    }

    @Test
    public void testHandleRoutineProposalNotAllowedException() {
        RoutineProposalNotAllowedException ex = new RoutineProposalNotAllowedException();
        when(messageSource.getMessage(eq("propose.exception"), any(), anyString(), any(Locale.class)))
                .thenReturn("Routine proposal not allowed");

        ErrorsDto result = advice.handleRoutineProposalNotAllowedException(ex, Locale.ENGLISH);

        assertNotNull(result);
    }

    @Test
    public void testHandleExerciseProposalNotAllowedException() {
        ExerciseProposalNotAllowedException ex = new ExerciseProposalNotAllowedException();
        when(messageSource.getMessage(eq("propose.exception"), any(), anyString(), any(Locale.class)))
                .thenReturn("Exercise proposal not allowed");

        ErrorsDto result = advice.handleExerciseProposalNotAllowedException(ex, Locale.ENGLISH);

        assertNotNull(result);
    }
}
