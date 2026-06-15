package es.udc.fi.dc.fd.model.services.exceptions;

/**
 * Excepción para credenciales de inicio de sesión incorrectas.
 */
@SuppressWarnings("serial")
public class IncorrectLoginException extends Exception {

	/** Nombre de usuario (login). */
	private final String username;

	/** Contraseña. */
	private final String password;

	/**
	 * Instancia una nueva IncorrectLoginException.
	 *
	 * @param username el nombre de usuario
	 * @param password la contraseña
	 */
	public IncorrectLoginException(String username, String password) {

		this.username = username;
		this.password = password;

	}

	/**
	 * Devuelve el nombre de usuario.
	 *
	 * @return el nombre de usuario
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Devuelve la contraseña.
	 *
	 * @return la contraseña
	 */
	public String getPassword() {
		return password;
	}

}
