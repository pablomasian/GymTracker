package es.udc.fi.dc.fd.rest.dtos;

import es.udc.fi.dc.fd.model.entities.User;

// Conversor entre entidad User y sus DTOs
public class UserConversor {

	/**
	 * Instancia un nuevo conversor de usuario.
	 */
	private UserConversor() {
	}

	/**
	 * Convierte a UserDto.
	 *
	 * @param user entidad de usuario
	 * @return el UserDto
	 */
	public static final UserDto toUserDto(User user) {
	UserDto dto = new UserDto(user.getId(), user.getNombreUsuario(), user.getFirstName(), user.getLastName(), user.getUsername(),
		user.getRole().toString());
	dto.setEmail(user.getEmail());
	dto.setAvatarUrl(user.getAvatarUrl());
	dto.setPremium(user.getPremium());
	dto.setBlocked(user.getBlocked());
	return dto;
	}

	/**
	 * Convierte a entidad User.
	 *
	 * @param userDto DTO de usuario
	 * @return la entidad User
	 */
	public static final User toUser(UserDto userDto) {
	User user = new User(userDto.getNombreUsuario(), userDto.getPassword(), userDto.getFirstName(), userDto.getLastName(),
		userDto.getUsername());
	user.setEmail(userDto.getEmail());
	user.setAvatarUrl(userDto.getAvatarUrl());
	user.setPremium(userDto.getPremium());
	if (userDto.getRole() != null) {
		try {
			user.setRole(User.RoleType.valueOf(userDto.getRole()));
		} catch (IllegalArgumentException ignored) {
		}
	}
	return user;
	}

    public static UserPrivateDto toUserPrivateDto(User user) {
        UserPrivateDto dto = new UserPrivateDto();
        dto.setId(user.getId());
        dto.setNombreUsuario(user.getNombreUsuario());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());

        dto.setWeight(user.getWeight());
        dto.setHeight(user.getHeight());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());

        return dto;
    }

    public static UserDto toUserPublicDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setNombreUsuario(user.getNombreUsuario());
        dto.setUsername(user.getUsername());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }


    /**
	 * Convierte a AuthenticatedUserDto.
	 *
	 * @param serviceToken token de servicio
	 * @param user         entidad de usuario
	 * @return el DTO de usuario autenticado
	 */
	public static final AuthenticatedUserDto toAuthenticatedUserDto(String serviceToken, User user) {

		return new AuthenticatedUserDto(serviceToken, toUserDto(user));

	}

}

