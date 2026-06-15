package es.udc.fi.dc.fd.rest.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

// DTO de usuario con validaciones y subgrupos (crear/actualizar)
public class UserDto {
	
	/**
	 * Interfaz de validaciones generales (crear/actualizar).
	 */
	public interface AllValidations {}
	
	public interface UpdateValidations {}

	/** El identificador. */
	private Long id;
	
	/** El nombre visible del usuario (nombre_usuario). */
	private String nombreUsuario;
	
	/** La contraseña. */
	private String password;
	
	/** El nombre. */
	private String firstName;
	
	/** Los apellidos. */
	private String lastName;
	
	/** El nombre de usuario usado para iniciar sesión (antes era el email). */
	private String username;
	
	/** Email opcional (para contacto/notificaciones). */
	private String email;

	/** URL de avatar opcional. */
	private String avatarUrl;
	
	/** Indica si el coach es premium (solo para coaches). */
	private Boolean premium;
	
	/** El rol. */
	private String role;
	
	/** Si el usuario está bloqueado. */
	private Boolean blocked;

	/**
	 * Crea un nuevo UserDto.
	 */
	public UserDto() {}

	/**
	 * Crea un nuevo UserDto.
	 *
	 * @param id el identificador
	 * @param firstName el nombre
	 * @param lastName los apellidos
	 * @param username el nombre de usuario para iniciar sesión
	 * @param role el rol
	 */
	public UserDto(Long id, String nombreUsuario, String firstName, String lastName, String username, String role) {
		this.id = id;
		this.nombreUsuario = nombreUsuario != null ? nombreUsuario.trim() : null;
		this.firstName = firstName != null ? firstName.trim() : null;
		this.lastName = lastName != null ? lastName.trim() : null;
		this.username = username != null ? username.trim() : null;
		this.role = role;
	}

	@Email
	@Size(max=120)
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = (email != null && !email.isBlank()) ? email.trim() : null; }

	public String getAvatarUrl() { return avatarUrl; }
	public void setAvatarUrl(String avatarUrl) { this.avatarUrl = (avatarUrl != null && !avatarUrl.isBlank()) ? avatarUrl.trim() : null; }

	/**
	 * Obtiene el estado premium del coach.
	 * 
	 * @return true si es coach premium, false si no premium, null si no es coach o no establecido
	 */
	public Boolean getPremium() { return premium; }
	
	/**
	 * Establece el estado premium del coach.
	 * 
	 * @param premium true para coach premium, false para no premium
	 */
	public void setPremium(Boolean premium) { this.premium = premium; }
    
	@NotNull(groups={AllValidations.class, UpdateValidations.class})
	@Size(min=1, max=60, groups={AllValidations.class, UpdateValidations.class})
	public String getNombreUsuario() {
		return nombreUsuario;
	}
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario != null ? nombreUsuario.trim() : null;
	}

	/**
	 * Obtiene el identificador.
	 *
	 * @return el id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador.
	 *
	 * @param id el nuevo id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	// Eliminado: getUserName/setUserName. Usar solo getUsername/setUsername.
	@NotNull(groups={AllValidations.class, UpdateValidations.class})
	@Size(min=1, max=60, groups={AllValidations.class, UpdateValidations.class})
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username != null ? username.trim() : null;
	}

	/**
	 * Obtiene la contraseña.
	 *
	 * @return la contraseña
	 */
	@NotNull(groups={AllValidations.class})
	@Size(min=1, max=60, groups={AllValidations.class})
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

	/**
	 * Obtiene el nombre.
	 *
	 * @return el nombre
	 */
	@NotNull(groups={AllValidations.class, UpdateValidations.class})
	@Size(min=1, max=60, groups={AllValidations.class, UpdateValidations.class})
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Establece el nombre.
	 *
	 * @param firstName el nuevo nombre
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName.trim();
	}

	/**
	 * Obtiene los apellidos.
	 *
	 * @return los apellidos
	 */
	@NotNull(groups={AllValidations.class, UpdateValidations.class})
	@Size(min=1, max=60, groups={AllValidations.class, UpdateValidations.class})
	public String getLastName() {
		return lastName;
	}

	/**
	 * Establece los apellidos.
	 *
	 * @param lastName los nuevos apellidos
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName.trim();
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

	/**
	 * Obtiene el estado de bloqueo.
	 *
	 * @return true si está bloqueado
	 */
	public Boolean getBlocked() {
		return blocked;
	}

	/**
	 * Establece el estado de bloqueo.
	 *
	 * @param blocked el nuevo estado
	 */
	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

}

