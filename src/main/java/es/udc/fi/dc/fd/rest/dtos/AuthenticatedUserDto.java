package es.udc.fi.dc.fd.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

// Respuesta de login: token de servicio + datos del usuario
public class AuthenticatedUserDto {

	/** Token de servicio. */
	private String serviceToken;
	
	/** DTO de usuario. */
	private UserDto userDto;

	/**
	 * Instancia un nuevo DTO de usuario autenticado.
	 */
	public AuthenticatedUserDto() {
	}

	/**
	 * Instancia un nuevo DTO de usuario autenticado.
	 *
	 * @param serviceToken token de servicio
	 * @param userDto DTO de usuario
	 */
	public AuthenticatedUserDto(String serviceToken, UserDto userDto) {

		setServiceToken(serviceToken);
		setUserDto(userDto);

	}

	/**
	 * Obtiene el token de servicio.
	 *
	 * @return el token de servicio
	 */
	public String getServiceToken() {
		return serviceToken;
	}

	/**
	 * Establece el token de servicio.
	 *
	 * @param serviceToken el nuevo token de servicio
	 */
	public void setServiceToken(String serviceToken) {
		this.serviceToken = serviceToken;
	}

	/**
	 * Obtiene el DTO de usuario.
	 *
	 * @return el DTO de usuario
	 */
	@JsonProperty("user")
	public UserDto getUserDto() {
		return userDto;
	}

	/**
	 * Establece el DTO de usuario.
	 *
	 * @param userDto el nuevo DTO de usuario
	 */
	public void setUserDto(UserDto userDto) {
		this.userDto = userDto;
	}

}
