package es.udc.fi.dc.fd.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario_GymTracker")
public class User {

	/**
	 * Enumeración con los tipos de rol.
	 */
	public enum RoleType {
		/** Rol de usuario. */
		USER,
		COACH,
		ADMIN
	}

	private Long id;
	private String nombreUsuario;
	private String password;
	private String firstName;
	private String lastName;
	private String username;
	private String email;
	private RoleType role;
	private String avatarUrl;
	private Boolean premium;
	private Boolean blocked;

	private Double weight;
	private Double height;
	private Integer age;
	private String gender;

	private Integer streakCount;
	private java.time.LocalDate lastTrainingDate;
	private java.time.LocalDateTime nextStreakDeadline;

	// Requerido por JPA (constructor sin argumentos)
	public User() {
	}

	// Crea un usuario con credenciales y datos básicos
	public User(String nombreUsuario, String password, String firstName, String lastName, String username) {
		this.nombreUsuario = nombreUsuario;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.role = RoleType.USER;
	}

	@Column(name = "nombre_usuario", length = 60, nullable = false)
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	/**
	 * Obtiene el identificador.
	 *
	 * @return el id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_usuario")
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

	/**
	 * Obtiene la contraseña.
	 *
	 * @return la contraseña
	 */
	@Column(name = "contrasena_usuario", length = 100, nullable = false)
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
	@Column(name = "first_name", length = 60)
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Establece el nombre.
	 *
	 * @param firstName el nuevo nombre
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Obtiene los apellidos.
	 *
	 * @return los apellidos
	 */
	@Column(name = "last_name", length = 60)
	public String getLastName() {
		return lastName;
	}

	/**
	 * Establece los apellidos.
	 *
	 * @param lastName los nuevos apellidos
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Obtiene el nombre de usuario (identificador de login).
	 *
	 * @return el nombre de usuario
	 */
	@Column(name = "username_usuario", length = 60, nullable = false, unique = true)
	public String getUsername() {
		return username;
	}

	/**
	 * Establece el nombre de usuario (identificador de login).
	 *
	 * @param username el nuevo nombre de usuario
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Obtiene el correo electrónico.
	 *
	 * @return el correo electrónico
	 */
	@Column(name = "email_usuario", length = 120, nullable = true)
	public String getEmail() {
		return email;
	}

	/**
	 * Establece el correo electrónico.
	 *
	 * @param email el nuevo correo electrónico
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Obtiene el rol.
	 *
	 * @return el rol
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "rol", length = 20, nullable = false)
	public RoleType getRole() {
		return role;
	}

	/**
	 * Establece el rol.
	 *
	 * @param role el nuevo rol
	 */
	public void setRole(RoleType role) {
		this.role = role;
	}

	@Column(name = "avatar_url", length = 255, nullable = true)
	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	/**
	 * Indica si el coach es premium (solo relevante para rol COACH).
	 * Coaches no premium tienen límites: máx 3 rutinas, 5 ejercicios/rutina, no
	 * pueden proponer ejercicios.
	 * 
	 * @return true si es coach premium, false si no premium, null si no es coach
	 */
	@Column(name = "premium", nullable = true)
	public Boolean getPremium() {
		return premium;
	}

	public void setPremium(Boolean premium) {
		this.premium = premium;
	}

	@Column(name = "bloqueado", nullable = false)
	public Boolean getBlocked() {
		return blocked != null && blocked;
	}

	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}
	

	@Column(name = "weight")
	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	@Column(name = "height")
	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	@Column(name = "age")
	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Column(name = "gender")
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	

	@Column(name = "streak_count", nullable = true)
	public Integer getStreakCount() {
		return streakCount;
	}

	public void setStreakCount(Integer streakCount) {
		this.streakCount = streakCount;
	}

	@Column(name = "last_training_date", nullable = true)
	public java.time.LocalDate getLastTrainingDate() {
		return lastTrainingDate;
	}

	public void setLastTrainingDate(java.time.LocalDate lastTrainingDate) {
		this.lastTrainingDate = lastTrainingDate;
	}

	@Column(name = "next_streak_deadline", nullable = true)
	public java.time.LocalDateTime getNextStreakDeadline() {
		return nextStreakDeadline;
	}

	public void setNextStreakDeadline(java.time.LocalDateTime nextStreakDeadline) {
		this.nextStreakDeadline = nextStreakDeadline;
	}

}
