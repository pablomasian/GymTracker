package es.udc.fi.dc.fd.model.services.exceptions;

/**
 * Excepción lanzada cuando un coach no premium intenta proponer un ejercicio.
 */
public class ExerciseProposalNotAllowedException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Instancia una nueva ExerciseProposalNotAllowedException.
	 */
	public ExerciseProposalNotAllowedException() {
		super("Non-premium coaches cannot propose new exercises");
	}

	/**
	 * Instancia una nueva ExerciseProposalNotAllowedException con mensaje personalizado.
	 *
	 * @param message mensaje de error
	 */
	public ExerciseProposalNotAllowedException(String message) {
		super(message);
	}

}
