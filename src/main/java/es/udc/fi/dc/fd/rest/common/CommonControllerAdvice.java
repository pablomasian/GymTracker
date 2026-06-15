package es.udc.fi.dc.fd.rest.common;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.services.exceptions.PermissionException;
import es.udc.fi.dc.fd.model.services.exceptions.MaxRoutinesExceededException;
import es.udc.fi.dc.fd.model.services.exceptions.MaxExercisesPerRoutineExceededException;
import es.udc.fi.dc.fd.model.services.exceptions.RoutineProposalNotAllowedException;
import es.udc.fi.dc.fd.model.services.exceptions.ExerciseProposalNotAllowedException;

// Manejo común de excepciones REST (validación, duplicados, no encontrado, permisos)
@ControllerAdvice
public class CommonControllerAdvice {

	/** Constante del código de error para InstanceNotFoundException. */
	private static final String INSTANCE_NOT_FOUND_EXCEPTION_CODE = "project.exceptions.InstanceNotFoundException";

	/** Constante del código de error para DuplicateInstanceException. */
	private static final String DUPLICATE_INSTANCE_EXCEPTION_CODE = "project.exceptions.DuplicateInstanceException";

	/** Constante del código de error para PermissionException. */
	private static final String PERMISSION_EXCEPTION_CODE = "project.exceptions.PermissionException";

	/** Constante del código de error para propuestas (coaches no premium) */
	private static final String PROPOSE_EXCEPTION_CODE = "propose.exception";

	/** Fuente de mensajes para i18n. */
	@Autowired
	private MessageSource messageSource;

	/**
	 * Maneja excepciones de validación de argumentos (@Valid).
	 *
	 * @param exception la excepción capturada
	 * @return el DTO de errores
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorsDto handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

		List<FieldErrorDto> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
				.map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage()))
				.collect(Collectors.toList());

		return new ErrorsDto(fieldErrors);

	}

	/**
	 * Maneja excepciones de instancia no encontrada.
	 *
	 * @param exception la excepción
	 * @param locale    la configuración regional
	 * @return el DTO de errores
	 */
	@ExceptionHandler(InstanceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorsDto handleInstanceNotFoundException(InstanceNotFoundException exception, Locale locale) {

		String nameMessage = messageSource.getMessage(exception.getName(), null, exception.getName(), locale);
		String defaultMsg = nameMessage + " with id " + exception.getKey().toString() + " not found.";
		String errorMessage = messageSource.getMessage(INSTANCE_NOT_FOUND_EXCEPTION_CODE,
			new Object[] { nameMessage, exception.getKey().toString() }, defaultMsg, locale);

		return new ErrorsDto(errorMessage);

	}

	/**
	 * Maneja excepciones de instancia duplicada.
	 *
	 * @param exception la excepción
	 * @param locale    la configuración regional
	 * @return el DTO de errores
	 */
	@ExceptionHandler(DuplicateInstanceException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorsDto handleDuplicateInstanceException(DuplicateInstanceException exception, Locale locale) {
		String rawName = exception.getName();
		String nameMessage = rawName;

		if (rawName != null && rawName.contains(".")) {
			nameMessage = rawName.substring(rawName.lastIndexOf('.') + 1);
		}

		if ("exercise".equalsIgnoreCase(nameMessage)) {
			nameMessage = "ejercicio";
		}

		String key = String.valueOf(exception.getKey());
		String errorMessage = "Ya existe " + nameMessage + " con valor «" + key + "».";
		return new ErrorsDto(errorMessage);
	}

	/**
	 * Maneja excepciones de permisos.
	 *
	 * @param exception la excepción
	 * @param locale    la configuración regional
	 * @return el DTO de errores
	 */
	@ExceptionHandler(PermissionException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public ErrorsDto handlePermissionException(PermissionException exception, Locale locale) {
		String defaultMsg = "You don't have permission to perform this action.";
		String errorMessage = messageSource.getMessage(PERMISSION_EXCEPTION_CODE, null, defaultMsg, locale);

		return new ErrorsDto(errorMessage);

	}

	/**
	 * Maneja excepciones de límite de rutinas excedido (coaches no premium).
	 *
	 * @param exception la excepción
	 * @return el DTO de errores
	 */
	@ExceptionHandler(MaxRoutinesExceededException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorsDto handleMaxRoutinesExceededException(MaxRoutinesExceededException exception) {
		return new ErrorsDto(exception.getMessage());
	}

	/**
	 * Maneja excepciones cuando un coach no premium intenta proponer/publicar una rutina.
	 * Devuelve el mensaje localizado usando la clave 'propose.exception' si existe.
	 */
	@ExceptionHandler(RoutineProposalNotAllowedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public ErrorsDto handleRoutineProposalNotAllowedException(RoutineProposalNotAllowedException exception, Locale locale) {
		String errorMessage = messageSource.getMessage(PROPOSE_EXCEPTION_CODE, null, exception.getMessage(), locale);
		return new ErrorsDto(errorMessage);
	}

	@ExceptionHandler(ExerciseProposalNotAllowedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public ErrorsDto handleExerciseProposalNotAllowedException(ExerciseProposalNotAllowedException exception, Locale locale) {
		String errorMessage = messageSource.getMessage(PROPOSE_EXCEPTION_CODE, null, exception.getMessage(), locale);
		return new ErrorsDto(errorMessage);
	}

	/**
	 * Maneja excepciones de límite de ejercicios por rutina excedido (coaches no premium).
	 *
	 * @param exception la excepción
	 * @return el DTO de errores
	 */
	@ExceptionHandler(MaxExercisesPerRoutineExceededException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorsDto handleMaxExercisesPerRoutineExceededException(MaxExercisesPerRoutineExceededException exception) {
		return new ErrorsDto(exception.getMessage());
	}

}
