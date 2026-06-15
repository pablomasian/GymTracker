package es.udc.fi.dc.fd.rest.common;

// DTO para errores de validación de campos
public class FieldErrorDto {

	/** Nombre del campo. */
	private String fieldName;
	
	/** Mensaje de error. */
	private String message;

	/**
	 * Instancia un nuevo DTO de error de campo.
	 *
	 * @param fieldName nombre del campo
	 * @param message mensaje asociado
	 */
	public FieldErrorDto(String fieldName, String message) {

		setFieldName(fieldName);
		setMessage(message);

	}

	/**
	 * Obtiene el nombre del campo.
	 *
	 * @return el nombre del campo
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Establece el nombre del campo.
	 *
	 * @param fieldName el nuevo nombre del campo
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Obtiene el mensaje.
	 *
	 * @return el mensaje
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Establece el mensaje.
	 *
	 * @param message el nuevo mensaje
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
