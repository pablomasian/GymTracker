package es.udc.fi.dc.fd.model.services;

import static org.junit.Assert.assertEquals;

import jakarta.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import es.udc.fi.dc.fd.model.common.exceptions.DuplicateInstanceException;
import es.udc.fi.dc.fd.model.common.exceptions.InstanceNotFoundException;
import es.udc.fi.dc.fd.model.entities.User;

/**
 * Tests del servicio de usuarios.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

	/** Servicio de usuarios. */
	@Autowired
	private UserService userService;

	/**
	 * Crea un usuario de prueba.
	 *
	 * @param userName nombre de usuario
	 * @return el usuario
	 */
	private User createUser(String userName) {
		// Usar un username que no choque con los datos semilla (seed usa 'user' y 'coach')
		return new User(userName, "password", "firstName", "lastName", userName);
	}

	/**
	 * Prueba de registro y login a partir del id.
	 *
	 * @throws DuplicateInstanceException si ya existe el usuario
	 * @throws InstanceNotFoundException  si no se encuentra el usuario
	 */
	@Test
	public void testSignUpAndLoginFromId() throws DuplicateInstanceException, InstanceNotFoundException {

		// Usar un username no presente en seed para evitar DuplicateInstanceException
		User user = createUser("user_test_unique");

		userService.signUp(user);

		User loggedInUser = userService.loginFromId(user.getId());

		assertEquals(user, loggedInUser);
		assertEquals(User.RoleType.USER, user.getRole());

	}
}
