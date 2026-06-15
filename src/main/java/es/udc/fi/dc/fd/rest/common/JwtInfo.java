package es.udc.fi.dc.fd.rest.common;

// Información codificada en el JWT (id de usuario, username y rol)
public class JwtInfo {

	/** Id de usuario. */
	private Long userId;

	/** Nombre de usuario (login). */
	private String username;

	/** Rol. */
	private String role;

	/**
	 * Instancia una nueva información de JWT.
	 *
	 * @param userId   id de usuario
	 * @param username nombre de usuario para login
	 * @param role     rol del usuario
	 */
	public JwtInfo(Long userId, String username, String role) {

		this.userId = userId;
		this.username = username;
		this.role = role;

	}

	/**
	 * Obtiene el id de usuario.
	 *
	 * @return el id de usuario
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * Establece el id de usuario.
	 *
	 * @param userId el nuevo id de usuario
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * Obtiene el nombre de usuario.
	 *
	 * @return el nombre de usuario
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Establece el nombre de usuario.
	 *
	 * @param username el nuevo nombre de usuario
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Obtiene el rol.
	 *
	 * @return el rol
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Establece el rol.
	 *
	 * @param role el nuevo rol
	 */
	public void setRole(String role) {
		this.role = role;
	}

}