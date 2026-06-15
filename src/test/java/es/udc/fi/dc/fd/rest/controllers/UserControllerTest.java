package es.udc.fi.dc.fd.rest.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.udc.fi.dc.fd.model.entities.User;
import es.udc.fi.dc.fd.model.entities.User.RoleType;
import es.udc.fi.dc.fd.model.entities.UserDao;
import es.udc.fi.dc.fd.model.services.exceptions.BlockedUserException;
import es.udc.fi.dc.fd.model.services.exceptions.IncorrectLoginException;
import es.udc.fi.dc.fd.rest.dtos.AuthenticatedUserDto;
import es.udc.fi.dc.fd.rest.dtos.LoginParamsDto;

/**
 * Tests del controlador de usuarios.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // 🔹 Desactiva seguridad en los tests
@ActiveProfiles("test")
@Transactional
public class UserControllerTest {
	
	/** Constante de contraseña. */
	private final static String PASSWORD = "password";

	/** MockMvc para pruebas HTTP. */
	@Autowired
	private MockMvc mockMvc;

	/** Codificador de contraseñas. */
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	/** DAO de usuarios. */
	@Autowired
	private UserDao userDao;

	/** Controlador de usuarios. */
	@Autowired
	private UserController userController;

	/**
	 * Crea un usuario autenticado de prueba.
	 *
	 * @param username nombre de usuario
	 * @param roleType tipo de rol
	 * @return el DTO de usuario autenticado
	 * @throws IncorrectLoginException si el login es incorrecto
	 * @throws BlockedUserException si el usuario está bloqueado
	 */
	private AuthenticatedUserDto createAuthenticatedUser(String username, RoleType roleType)
			throws IncorrectLoginException, BlockedUserException {

	User user = new User(username, PASSWORD, "newUser", "user", username);

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(roleType);

		userDao.save(user);

		LoginParamsDto loginParams = new LoginParamsDto();
		loginParams.setUsername(user.getUsername());
		loginParams.setPassword(PASSWORD);

		return userController.login(loginParams);

	}

	/**
	 * Prueba de login correcto vía POST.
	 *
	 * @throws Exception en caso de error en la petición
	 */
	@Test
	public void testPostLogin_Ok() throws Exception {

		AuthenticatedUserDto user = createAuthenticatedUser("admin", RoleType.USER);

		LoginParamsDto loginParams = new LoginParamsDto();
		loginParams.setUsername(user.getUserDto().getUsername());
		loginParams.setPassword(PASSWORD);

		ObjectMapper mapper = new ObjectMapper();

		// 🔹 No pasamos Authorization, login no lo necesita
		mockMvc.perform(post("/api/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(loginParams)))
				.andExpect(status().isOk());

	}
}
