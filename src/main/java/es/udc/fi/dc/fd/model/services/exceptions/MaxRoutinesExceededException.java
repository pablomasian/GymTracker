package es.udc.fi.dc.fd.model.services.exceptions;

/**
 * Excepción lanzada cuando un coach no premium intenta crear más de 3 rutinas.
 */
public class MaxRoutinesExceededException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Instancia una nueva MaxRoutinesExceededException.
	 */
	public MaxRoutinesExceededException() {
		super("Non-premium coaches can create a maximum of 3 routines");
	}

	/**
	 * Instancia una nueva MaxRoutinesExceededException con mensaje personalizado.
	 *
	 * @param message mensaje de error
	 */
	public MaxRoutinesExceededException(String message) {
		super(message);
	}

}
