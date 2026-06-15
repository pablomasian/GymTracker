package es.udc.fi.dc.fd.model.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.services.exceptions.BlockedUserException;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectLoginException;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectPasswordException;

/**
 * Interfaz del servicio de usuarios.
 */
public interface UserService {
	
	/**
	 * Registro de usuario.
	 *
	 * @param user el usuario
	 * @throws DuplicateInstanceException si ya existe un usuario con los mismos datos
	 */
	void signUp(User user) throws DuplicateInstanceException;

	User saveUserAvatar(String username, MultipartFile file) throws IOException;

	
	/**
	 * Inicio de sesión.
	 *
	 * @param userName nombre de usuario
	 * @param password contraseña
	 * @return el usuario autenticado
	 * @throws IncorrectLoginException si las credenciales no son correctas
	 * @throws BlockedUserException si el usuario está bloqueado
	 */
	User login(String userName, String password) throws IncorrectLoginException, BlockedUserException;
	
	/**
	 * Inicio de sesión a partir del id.
	 *
	 * @param id identificador del usuario
	 * @return el usuario
	 * @throws InstanceNotFoundException si no se encuentra el usuario
	 */
	User loginFromId(Long id) throws InstanceNotFoundException;
	
	/**
	 * Actualiza el perfil.
	 *
	 * @param id identificador
	 * @param firstName nombre
	 * @param lastName apellidos
	 * @param username nombre de usuario (identificador de login)
	 * @param nombreUsuario nombre para mostrar
	 * @param email correo electrónico
	 * @param premium estado premium (solo para coaches)
	 * @return el usuario actualizado
	 * @throws InstanceNotFoundException si no se encuentra el usuario
	 */



    User updateProfile(Long id,
                       String firstName,
                       String lastName,
                       String username,
                       String nombreUsuario,
                       String email,
                       Double weight,
                       Double height,
                       Integer age,
                       String gender,
                       Boolean premium) throws InstanceNotFoundException;


    /**
	 * Cambia la contraseña.
	 *
	 * @param id identificador
	 * @param oldPassword contraseña actual
	 * @param newPassword contraseña nueva
	 * @throws InstanceNotFoundException si no se encuentra el usuario
	 * @throws IncorrectPasswordException si la contraseña actual no es válida
	 */
	void changePassword(Long id, String oldPassword, String newPassword)
		throws InstanceNotFoundException, IncorrectPasswordException;


	User getCoachProfile (Long coach_id);
	
	/**
	 * Lista todos los usuarios (solo para admin).
	 *
	 * @return lista de usuarios
	 */
	java.util.List<User> listAllUsers();
	
	/**
	 * Bloquea un usuario (solo admin).
	 *
	 * @param userId identificador del usuario
	 * @throws InstanceNotFoundException si no se encuentra el usuario
	 */
	void blockUser(Long userId) throws InstanceNotFoundException;
	
	/**
	 * Desbloquea un usuario (solo admin).
	 *
	 * @param userId identificador del usuario
	 * @throws InstanceNotFoundException si no se encuentra el usuario
	 */
	void unblockUser(Long userId) throws InstanceNotFoundException;


	/**
	 * Actualiza el estado premium de un usuario.
	 *
	 * @param userId identificador del usuario
	 * @param premium nuevo estado premium (true para premium, false para básico)
	 * @return el usuario actualizado
	 * @throws InstanceNotFoundException si no se encuentra el usuario
	 */
	User updatePremiumStatus(Long userId, Boolean premium) throws InstanceNotFoundException;
	
	List<User> searchUsers(String query, Long requesterId);

    User getPublicUserProfile(Long id) throws InstanceNotFoundException;

}
