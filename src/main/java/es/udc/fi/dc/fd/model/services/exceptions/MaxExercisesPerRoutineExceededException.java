package es.udc.fi.dc.fd.model.services.exceptions;

/**
 * Excepción lanzada cuando un coach no premium intenta añadir más de 5 ejercicios a una rutina.
 */
public class MaxExercisesPerRoutineExceededException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Instancia una nueva MaxExercisesPerRoutineExceededException.
	 */
	public MaxExercisesPerRoutineExceededException() {
		super("Non-premium coaches can add a maximum of 5 exercises per routine");
	}

	/**
	 * Instancia una nueva MaxExercisesPerRoutineExceededException con mensaje personalizado.
	 *
	 * @param message mensaje de error
	 */
	public MaxExercisesPerRoutineExceededException(String message) {
		super(message);
	}

}
