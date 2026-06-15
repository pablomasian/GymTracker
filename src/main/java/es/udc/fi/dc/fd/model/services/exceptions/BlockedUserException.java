package es.udc.fi.dc.fd.model.services.exceptions;

/**
 * Excepción para usuarios bloqueados por el administrador.
 */
@SuppressWarnings("serial")
public class BlockedUserException extends Exception {

	/** Nombre de usuario bloqueado. */
	private final String username;

	/**
	 * Instancia una nueva BlockedUserException.
	 *
	 * @param username el nombre de usuario bloqueado
	 */
	public BlockedUserException(String username) {
		super("User '" + username + "' has been blocked by administrator");
		this.username = username;
	}

	/**
	 * Devuelve el nombre de usuario bloqueado.
	 *
	 * @return el nombre de usuario
	 */
	public String getUsername() {
		return username;
	}

}
