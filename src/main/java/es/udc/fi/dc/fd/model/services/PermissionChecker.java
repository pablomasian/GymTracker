package es.udc.fi.dc.fd.model.services;

import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.User;

/**
 * Interfaz para comprobación de permisos y existencia de usuarios.
 */
public interface PermissionChecker {
	
	/**
	 * Comprueba que el usuario existe.
	 *
	 * @param userId id del usuario
	 * @throws InstanceNotFoundException si no se encuentra el usuario
	 */
	public void checkUserExists(Long userId) throws InstanceNotFoundException;
	
	/**
	 * Recupera el usuario comprobando su existencia.
	 *
	 * @param userId id del usuario
	 * @return el usuario
	 * @throws InstanceNotFoundException si no se encuentra el usuario
	 */
	public User checkUser(Long userId) throws InstanceNotFoundException;
	
}
