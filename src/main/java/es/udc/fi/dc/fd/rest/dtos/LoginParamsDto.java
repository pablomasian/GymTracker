package es.udc.fi.dc.fd.rest.dtos;

import jakarta.validation.constraints.NotNull;

// Parámetros de login (username y password)
public class LoginParamsDto {

	/** Nombre de usuario para el login. */
	private String username;

	/** Contraseña. */
	private String password;

	/**
	 * Instancia un nuevo DTO de parámetros de login.
	 */
	public LoginParamsDto() {
		super();
	}

	/**
	 * Obtiene el nombre de usuario.
	 *
	 * @return el nombre de usuario
	 */
	@NotNull
	public String getUsername() {
		return username;
	}

	/**
	 * Establece el nombre de usuario.
	 *
	 * @param username el nuevo nombre de usuario
	 */
	public void setUsername(String username) {
		this.username = username.trim();
	}

	/**
	 * Obtiene la contraseña.
	 *
	 * @return la contraseña
	 */
	@NotNull
	public String getPassword() {
		return password;
	}

	/**
	 * Establece la contraseña.
	 *
	 * @param password la nueva contraseña
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
