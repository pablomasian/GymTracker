package es.udc.fi.dc.fd.rest.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Parámetros para cambiar la contraseña
public class ChangePasswordParamsDto {

	/** Contraseña actual. */
	private String oldPassword;

	/** Contraseña nueva. */
	private String newPassword;

	/**
	 * Instancia un nuevo DTO de cambio de contraseña.
	 */
	public ChangePasswordParamsDto() {
		super();
	}

	/**
	 * Obtiene la contraseña actual.
	 *
	 * @return la contraseña actual
	 */
	@NotNull
	public String getOldPassword() {
		return oldPassword;
	}

	/**
	 * Establece la contraseña actual.
	 *
	 * @param oldPassword la contraseña actual
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	/**
	 * Obtiene la nueva contraseña.
	 *
	 * @return la nueva contraseña
	 */
	@NotNull
	@Size(min = 1, max = 60)
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * Establece la nueva contraseña.
	 *
	 * @param newPassword la nueva contraseña
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
