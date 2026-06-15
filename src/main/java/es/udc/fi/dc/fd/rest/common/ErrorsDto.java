package es.udc.fi.dc.fd.rest.common;

import java.util.List;

// DTO de errores para respuestas: global y por campo
public class ErrorsDto {

	/** Error global. */
	private String globalError;
	
	/** Errores por campo. */
	private List<FieldErrorDto> fieldErrors;

	/**
	 * Instancia un nuevo DTO de errores.
	 *
	 * @param globalError el error global
	 */
	public ErrorsDto(String globalError) {
		setGlobalError(globalError);
	}

	/**
	 * Instancia un nuevo DTO de errores.
	 *
	 * @param fieldErrors los errores por campo
	 */
	public ErrorsDto(List<FieldErrorDto> fieldErrors) {
		setFieldErrors(fieldErrors);
	}

	/**
	 * Obtiene el error global.
	 *
	 * @return el error global
	 */
	public String getGlobalError() {
		return globalError;
	}

	/**
	 * Establece el error global.
	 *
	 * @param globalError el nuevo error global
	 */
	public void setGlobalError(String globalError) {
		this.globalError = globalError;
	}

	/**
	 * Obtiene los errores por campo.
	 *
	 * @return los errores por campo
	 */
	public List<FieldErrorDto> getFieldErrors() {
		return fieldErrors;
	}

	/**
	 * Establece los errores por campo.
	 *
	 * @param fieldErrors los nuevos errores por campo
	 */
	public void setFieldErrors(List<FieldErrorDto> fieldErrors) {
		this.fieldErrors = fieldErrors;
	}

}
